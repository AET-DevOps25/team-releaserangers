# Python microservice for document summarization using LangChain and Docling
#
# This service receives documents (PDF, Markdown, images, etc.), extracts content using Docling,
# and generates a summary using a genai endpoint.

from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import JSONResponse
from typing import List
import tempfile
import shutil
import os
import requests

# Import LangChain and Docling (assume installed)
from langchain.text_splitter import RecursiveCharacterTextSplitter
# from docling import Docling
from langchain_docling import DoclingLoader

app = FastAPI()

GENAI_ENDPOINT = "http://localhost:8000/genai/summarize"


def extract_text(file_path: str, filename: str) -> str:
    """
    Use Docling to extract text from the given file.
    """
    try:
        doc_loader = DoclingLoader(file_path)
        docs = doc_loader.load()
        for d in docs[:3]:
            print(f"- {d.page_content=}")
        return docs
    except Exception as e:
        raise RuntimeError(f"Docling extraction failed: {e}")


def summarize_text(text: str) -> str:
    """
    Send the extracted text to the genai endpoint for summarization.
    """

    return f"This is my summary from {GENAI_ENDPOINT}."

    # response = requests.post(GENAI_ENDPOINT, json={"text": text})
    # if response.status_code == 200:
    #     return response.json().get("summary", "")
    # else:
    #     raise RuntimeError(f"GenAI endpoint error: {response.text}")


@app.post("/summarize")
async def summarize_document(file: UploadFile = File(...)):
    print("Save uploaded file to a temp location (Replace with storing in db?)")
    with tempfile.NamedTemporaryFile(delete=False, suffix=os.path.splitext(file.filename)[1]) as tmp:
        shutil.copyfileobj(file.file, tmp)
        tmp_path = tmp.name

    try:
        print(f"Extract text using Docling, path: {tmp_path} filename: {file.filename}")
        text = extract_text(tmp_path, file.filename)
        print(f"Extracted text using Docling: {text[0]}")
        if not text:
            raise HTTPException(status_code=400, detail="No text extracted from document.")

        # Optionally split text if too long (LangChain)
        splitter = RecursiveCharacterTextSplitter(chunk_size=2000, chunk_overlap=200)
        chunks = splitter.split_text(text[0].page_content)
        summaries = [summarize_text(chunk) for chunk in chunks]
        summary = "\n".join(summaries)
        return JSONResponse(content={"summary": summary})
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        os.remove(tmp_path)
