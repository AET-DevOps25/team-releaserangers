from pydantic import BaseModel

class SummaryResponse(BaseModel):
    chapter_title: str
    summary_markdown: str
    emoji: str
    source_file: str
