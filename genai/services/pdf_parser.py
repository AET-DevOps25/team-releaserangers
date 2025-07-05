from io import BytesIO
from docling.document_converter import DocumentConverter
from docling.datamodel.base_models import DocumentStream
from docling.datamodel.base_models import InputFormat
from docling.datamodel.pipeline_options import PdfPipelineOptions
from docling.document_converter import DocumentConverter, PdfFormatOption
from fastapi import UploadFile

import tempfile, pathlib
from langchain_docling import DoclingLoader
from langchain_docling.loader import ExportType
from langchain_core.documents import Document

from fastapi import UploadFile

async def extract_markdown_langchain(file: UploadFile) -> str:
    contents = await file.read()
    suffix = pathlib.Path(file.filename).suffix or ".pdf"

    # create tempfile as LangChain's DoclingLoader only accepts file paths as input
    with tempfile.NamedTemporaryFile(suffix=suffix, delete=False) as tmp:
        tmp.write(contents)
        tmp.flush()
        tmp_path = tmp.name

    # Set pipeline options
    pipeline_options = PdfPipelineOptions(do_ocr=False, do_table_structure=False)

    # Initialize the DocumentConverter
    converter = DocumentConverter(
        format_options={
            InputFormat.PDF: PdfFormatOption(pipeline_options=pipeline_options)
        }
    )

    # DoclingLoader accepts file bytes via stream argument
    loader = DoclingLoader(
        file_path=tmp_path,
        converter=converter,
        export_type=ExportType.MARKDOWN,        # or DOC_CHUNKS
        md_export_kwargs={},
    )

    docs = loader.load()
    markdown = "\n\n".join([doc.page_content for doc in docs])
    return markdown

# Currently not used
async def extract_markdown(file: UploadFile) -> str:
 # Read the uploaded file into memory
    file_bytes = await file.read()
    buffer = BytesIO(file_bytes)


    # Create a DocumentStream for Docling
    source = DocumentStream(name=file.filename, stream=buffer)

    # Set pipeline options
    pipeline_options = PdfPipelineOptions(do_ocr=False, do_table_structure=False)

    # Initialize the DocumentConverter
    converter = DocumentConverter(
        format_options={
            InputFormat.PDF: PdfFormatOption(pipeline_options=pipeline_options)
        }
    )   

    # Convert the document
    result = converter.convert(source)

    # Export the result to Markdown
    return result.document.export_to_markdown()