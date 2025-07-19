[← Back to Main README](../README.md)

---

# GenAI Service Testing

This directory contains comprehensive unit tests for the GenAI service.

## Test Structure

- `test_llm_provider.py` - Tests for LLM provider classes and functions
- `test_llm_clients.py` - Tests for LLM client functions
- `test_summarizer.py` - Tests for summarizer service
- `test_pdf_parser.py` - Tests for PDF parsing functionality
- `test_summary.py` - Tests for summary models
- `test_summarize_route.py` - Tests for API routes

## Running Tests

### Prerequisites

Install test dependencies additionally to the main requirements:

```bash
pip install -r requirements-test.txt
```

### Run All Tests

```bash
# Using pytest directly
pytest tests/ -v

# Using the test script (recommended)
chmod +x run_tests.sh
./run_tests.sh
```

### Run Specific Test Files

```bash
pytest tests/test_llm_provider.py -v
pytest tests/test_summarize_route.py -v
```

### Run Specific Test Cases

```bash
pytest tests/test_llm_provider.py::TestGenericLLM::test_call_google_backend_success -v
```

## Test Features

### Mocking

All tests use mocking to avoid:

- Actual LLM API calls
- File system operations
- External HTTP requests
- Database connections
