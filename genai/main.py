from dotenv import load_dotenv
load_dotenv()

import os
from fastapi import FastAPI
from routes import summarize

print("CHAIR_API_KEY:", os.getenv("CHAIR_API_KEY"))
app = FastAPI(
    title="LLM Summarization Service",
    description="Service that parses uploaded documents (e.g. pdfs) and returns a summary of that pdf in markdown format.",
    version="1.0.0"
)
app.include_router(summarize.router)
