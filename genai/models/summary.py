from pydantic import BaseModel, Field

class SummaryResponse(BaseModel):
    chapter_title: str
    summary_markdown: str
    emoji: str
    source_file: str

class SummarySchema(BaseModel):
    chapter_title: str = Field(..., description="Short title for the chapter")
    summary_markdown: str = Field(..., description="Markdown-formatted summary of the lecture")
    emoji: str = Field(..., description="Relevant academic emoji")

   
