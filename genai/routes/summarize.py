import json
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
    markdown = await pdf_parser.extract_markdown_langchain(file)
    summary_string = await summarizer.summarize(markdown)

    # clean string of markdown formatting
    summary_clean = summary_string.strip()

    if summary_clean.startswith("```json"):
        summary_clean = summary_clean[len("```json"):]

    if summary_clean.endswith("```"):
        summary_clean = summary_clean[:-len("```")]

    summary_clean = summary_clean.strip()
    print("CLEAN:", summary_clean)
    summary_json = json.loads(summary_clean)
    return SummaryResponse(
        chapter_title=summary_json["chapter_title"],
        summary_markdown=summary_json["summary_markdown"],
        emoji=summary_json["emoji"],
        source_file=file.filename
    )
