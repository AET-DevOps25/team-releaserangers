from pydantic import BaseModel
from typing import List
from langchain_core.documents import Document

class SummaryResponse(BaseModel):
    chapter_title: str
    summary_markdown: str
    emoji: str
    source_file: str
