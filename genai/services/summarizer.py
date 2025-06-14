from utils.llm_clients import summarize_with_llm

async def summarize(markdown_text: str) -> str:
    return await summarize_with_llm(markdown_text)
