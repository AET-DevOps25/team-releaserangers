from fastapi import APIRouter, UploadFile, File
from services import pdf_parser, summarizer
from models.summary import SummaryResponse

router = APIRouter()

@router.post("/summarize", response_model=SummaryResponse)
async def summarize_pdf(file: UploadFile = File(...)):
    markdown = await pdf_parser.extract_markdown(file)
    summary = await summarizer.summarize(markdown)
    return SummaryResponse(
        chapter_title=summary["chapter_title"],
        summary_markdown=summary["summary_markdown"],
        emoji=summary["emoji"],
        source_file=file.filename
    )
