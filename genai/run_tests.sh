#!/bin/bash

# Test script for the GenAI service
# This script runs all tests with proper environment setup

echo "Setting up test environment..."

# Create virtual environment if it doesn't exist
if [ ! -d "venv" ]; then
    echo "Creating virtual environment..."
    python3 -m venv venv
fi

# Activate virtual environment
source venv/bin/activate

# Install test dependencies
echo "Installing test dependencies..."
pip install -r requirements.txt
pip install -r requirements-test.txt

# Set test environment variables
export PYTHONPATH="${PYTHONPATH}:$(pwd)"
export LLM_API_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite-preview-06-17:generateContent"
export LLM_API_KEY="test-key" # pragma: allowlist secret
export LLM_MODEL="gemini-2.5-flash-lite-preview-06-17"
export LLM_BACKEND="google"
export COURSEMGMT_URL="http://localhost:8081"
export FILE_PARSING=False

# Run tests
echo "Running tests..."
python -m pytest tests/ -v --tb=short

# Check test results
if [ $? -eq 0 ]; then
    echo "All tests passed!"
else
    echo "Some tests failed!"
    exit 1
fi

echo "Test run completed."