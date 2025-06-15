import json
import time
import re
from fastapi import APIRouter, UploadFile, File
from services import pdf_parser, summarizer
from models.summary import SummaryResponse

router = APIRouter()

@router.post(
        "/summarize",
        response_model=SummaryResponse,
        summary="Generate summary for uploaded file.",
        description="Accepts user's uploaded file and returns a summary via Ollama."
        )
async def summarize_pdf(file: UploadFile = File(...)):
    """
    Generate a summary of the given file using LangChain and Ollama.
    
    Args:
        file: The request containing the uploaded file.
        
    Returns:
        SummaryResponse containing the summary and metadata.
        
    Raises:
        HTTPException: If the API call fails or other errors occur
    """
    start_extract = time.perf_counter()
    markdown = await pdf_parser.extract_markdown_langchain(file)
    end_extract = time.perf_counter()
    print(f"PDF extraction took {end_extract - start_extract:.2f} seconds")

    start_summary = time.perf_counter()
    summary_string = await summarizer.summarize(markdown)
    end_summary = time.perf_counter()
    print(f"Summarization took {end_summary - start_summary:.2f} seconds")

    def clean_markdown(llm_str):
        # clean string of markdown formatting
        summary_clean = llm_str.strip()

        if summary_clean.startswith("```json"):
            summary_clean = summary_clean[len("```json"):]

        if summary_clean.endswith("```"):
            summary_clean = summary_clean[:-len("```")]
        
        summary_clean = summary_clean.strip()
        
        # Fix: Escape all raw line breaks inside string values
        # This regex finds line breaks *inside* strings
        def escape_invalid_newlines(match):
            return match.group(0).replace("\n", "\\n")

        # This regex matches JSON string values
        llm_str = re.sub(r'".*?(?<!\\)"', escape_invalid_newlines, summary_clean, flags=re.DOTALL)
        return llm_str
    
    md_summary_clean = clean_markdown(summary_string)
    try:
        summary_json = json.loads(md_summary_clean)
    except json.JSONDecodeError as e:
        raise Exception(f"JSON decoding failed. Offending string: {md_summary_clean[:1000]}")


    return SummaryResponse(
        chapter_title=summary_json["chapter_title"],
        summary_markdown=summary_json["summary_markdown"],
        emoji=summary_json["emoji"],
        source_file=file.filename
    )
