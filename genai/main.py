from dotenv import load_dotenv
from fastapi import FastAPI
from routes import summarize
from prometheus_fastapi_instrumentator import Instrumentator
import os
load_dotenv()

app = FastAPI(
    title="LLM Summarization Service",
    description="Service that parses uploaded documents (e.g. pdfs) and returns a summary of that pdf in markdown format.",
    version="1.0.0"
)
app.include_router(summarize.router)

Instrumentator().instrument(app).expose(app)

# Entry point for direct execution
if __name__ == "__main__":
    """
    Entry point for `python main.py` invocation.
    Starts Uvicorn server serving this FastAPI app.

    Honors PORT environment variable (default: 8084).
    Reload=True enables live-reload during development.
    """
    import uvicorn

    port = int(os.getenv("PORT", 8000))
    
    print(f"Starting LLM Recommendation Service on port {port}")
    print(f"API Documentation available at: http://localhost:{port}/docs")
    
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=port,
        reload=True
    )
