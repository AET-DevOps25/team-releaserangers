from typing import Optional, BinaryIO
from utils.llm_clients import summarize_with_llm

async def summarize(
    markdown_text: Optional[str] = None,
    file: Optional[BinaryIO] = None,
    filename: Optional[str] = None
) -> str:
    return await summarize_with_llm(lecture_text=markdown_text, file=file, filename=filename)