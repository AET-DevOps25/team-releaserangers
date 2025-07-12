from langchain_core.prompts import PromptTemplate
from utils.llm_provider import get_llm

async def summarize_with_llm(lecture_content: str) -> str:
   
    # Initialize the LLM
    llm = get_llm()

    model_name = getattr(llm, "model_name", None) or getattr(llm, "model", None) or "Unknown model"
    print(f"Using {model_name} for summarization")

    # Create the prompt template
    summary_prompt = PromptTemplate(
    input_variables=["lecture_text"],
    template="""
            You are an academic summarization assistant. Your task is to process a student's raw lecture notes and generate:
            1. A concise and descriptive **chapter title** (max 10 words).
            2. A suitable **emoji** representing the topic (academic, not playful).
            3. A clean, structured, exhaustive **Markdown-formatted summary** of the lecture notes.

            Strict requirements:
            - **Use only the information contained in the input text.**
            - **Do not add external knowledge or inferred content.**
            - **Do not hallucinate or make assumptions.**
            - Avoid redundancy; keep it clear and comprehensive.
            - The summary should **accurately reflect the structure and depth** of the source material.
            - Avoid redundant information and unnecessary repetition.
            - The final output of the summary must be formatted in **Markdown**, using appropriate structural elements:
                - `#` for main topics
                - `##`, `###` for subtopics
                - Bullet points or numbered lists where appropriate
                - Code blocks, formulas, or other notation if they appear in the notes
            - The title must summarize the central topic.
            - Pick a relevant emoji: e.g., 📘, 🧠, 📐, 💻, 🧪 — avoid informal or irrelevant ones.
            - Return a valid JSON object, with all newlines inside string values escaped as \\n
            Respond with **only** the JSON object and **nothing else**. Do not include any explanation, commentary, or formatting outside the JSON.

            Input Lecture Notes:
            --------------------
            {lecture_text}
            --------------------

            Now, generate a clear, well-organized **Markdown-formatted** summary based on the content above. The output should be suitable for exam preparation.
            Respond in the following **JSON format**:
            {{
            "chapter_title": "Generated title here",
            "summary_markdown": "Markdown summary here",
            "emoji": "🎓",
            }}
           
            """
            )
    
    # Create the chain using the new RunnableSequence
    summary_chain = summary_prompt | llm

    # Use LangChain to create the summary
    summary = summary_chain.invoke(lecture_content)
    return summary
