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
    summary = await summarizer.summarize(markdown)
    end_summary = time.perf_counter()
    print(f"Summarization took {end_summary - start_summary:.2f} seconds")

    return SummaryResponse(
        chapter_title=summary.chapter_title,
        summary_markdown=summary.summary_markdown,
        emoji=summary.emoji,
        source_file=file.filename
    )
