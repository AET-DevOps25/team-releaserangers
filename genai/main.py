from fastapi import FastAPI
from routes import summarize

app = FastAPI()
app.include_router(summarize.router)
