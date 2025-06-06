async def summarize_with_llm(markdown: str) -> str:
    # Mocked response — just return the input for now
    return {
        "chapter_title": "Mocked Chapter Title",
        "summary_markdown": markdown, 
        "emoji": "📘"
    }
