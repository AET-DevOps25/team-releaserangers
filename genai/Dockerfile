# Use official Python image
FROM python:3.11-slim

# Set working directory
WORKDIR /app

# Copy requirements and install
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy app files
COPY . .

# Use uvicorn to run the app
CMD ["uvicorn", "main:app", "--host", "127.0.0.1", "--port", "8082"]
