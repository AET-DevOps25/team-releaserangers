import os
import json
import time
import re
import httpx
from fastapi import APIRouter, Cookie, UploadFile, File, Form, HTTPException
from typing import List, Optional
from services import pdf_parser, summarizer
from models.summary import SummaryResponse

router = APIRouter()

COURSEMGMT_URL = os.getenv("COURSEMGMT_URL", "http://localhost:8081")

@router.post(
        "/summarize",
        response_model=List[SummaryResponse],
        summary="Generate summary for uploaded file(s).",
        description="Accepts files and course metadata to return a summary."
        )
async def summarize_pdf(
    courseId: str = Form(...),
    existingChapterSummary: str = Form(...), # will be JSON string
    files: List[UploadFile] = File(...),
    token: Optional[str] = Cookie(None)
):
    """
    Generate a summary of the given file(s).
    
    Args:
        files: The request containing the uploaded file(s).
        
    Returns:
        List of SummaryResponse containing the summaries and metadata for each file.
        
    Raises:
        HTTPException: If the API call fails or other errors occur
    """

    if token is None:
        raise HTTPException(status_code=401, detail="Missing Cookie")
    # Parse existing summary JSON

    try:
        existing_summary_data = json.loads(existingChapterSummary)
    except json.JSONDecodeError as e:
        raise Exception(f"Invalid JSON in existingChapterSummary: {str(e)}")

    summaries = []
    for file in files:
        print(f"Processing file: {file.filename}")

        # Extract markdown
        start_extract = time.perf_counter()
        docs = await pdf_parser.extract_markdown_langchain(file)  # returns list
        markdown = "\n\n".join(docs)
        end_extract = time.perf_counter()
        print(f"PDF extraction took {end_extract - start_extract:.2f} seconds")

        # Summarize
        start_summary = time.perf_counter()
        summary_string = await summarizer.summarize(markdown)
        end_summary = time.perf_counter()
        print(f"Summarization took {end_summary - start_summary:.2f} seconds")

        # Clean summary
        cleaned = clean_markdown(summary_string)
        print(cleaned)
        try:
            summary_json = json.loads(cleaned)
        except json.JSONDecodeError:
            raise HTTPException(status_code=500, detail="LLM returned invalid JSON")

        # Prepare payload for chapter service
        chapter_payload = {
            "title": summary_json["chapter_title"],
            "content": summary_json["summary_markdown"],
            "emoji": summary_json["emoji"],
            "isFavorite": False,
        }
        print(f"{COURSEMGMT_URL}/{courseId}/chapters")
        # Send to chapter backend
        async with httpx.AsyncClient() as client:
           
            chapter_resp = await client.post(
                f"{COURSEMGMT_URL}/courses/{courseId}/chapters",
                #headers={"Authorization": authorization},
                cookies={"token": token},
                json=chapter_payload
            )
            if chapter_resp.status_code != 200:
                raise HTTPException(status_code=chapter_resp.status_code,
                                    detail=f"Failed to save chapter for file {file.filename}")

        summaries.append(SummaryResponse(
            chapter_title=summary_json["chapter_title"],
            summary_markdown=summary_json["summary_markdown"],
            emoji=summary_json["emoji"],
            source_file=file.filename
        ))

    return summaries

def clean_markdown(llm_str: str) -> str:
    summary_clean = llm_str.strip()

    if summary_clean.startswith("```json"):
        summary_clean = summary_clean[len("```json"):]
    if summary_clean.endswith("```"):
        summary_clean = summary_clean[:-len("```")]

    summary_clean = summary_clean.strip()

    def escape_invalid_newlines(match):
        return match.group(0).replace("\n", "\\n")

    return re.sub(r'".*?(?<!\\)"', escape_invalid_newlines, summary_clean, flags=re.DOTALL)
