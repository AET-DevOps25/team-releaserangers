# Summary Service

## Setup:


1. Install Python 3.12:
    ```
    brew install python@3.12
    ```

2. Create python venv: 
    ```
    python3.12 -m venv .venv
    ```

3. Activate python venv:
    ```
    source .venv/bin/activate
    ```

4. Install requirements:
    ```
    pip install -r server/summary-service/requirements.txt
    ```

## How to start:

```
uvicorn server.summary-service.summary_service:app --reload
```

POST to: http://127.0.0.1:8000/summarize