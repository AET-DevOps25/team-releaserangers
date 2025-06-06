from io import BytesIO
from docling.document_converter import DocumentConverter
from docling.datamodel.base_models import DocumentStream
from docling.datamodel.base_models import InputFormat
from docling.datamodel.pipeline_options import PdfPipelineOptions
from docling.document_converter import DocumentConverter, PdfFormatOption
from fastapi import UploadFile


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