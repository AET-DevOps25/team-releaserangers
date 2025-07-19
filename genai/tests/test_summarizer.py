import pytest
import os
import sys
from unittest.mock import Mock, patch

# Add the parent directory to the path to import the application modules
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from services.summarizer import summarize


class TestSummarizer:
    """Test cases for summarizer service"""
    
    @pytest.mark.asyncio
    @patch('services.summarizer.summarize_with_llm')
    async def test_summarize_with_markdown_text(self, mock_summarize_with_llm):
        """Test summarize function with markdown text"""
        mock_summarize_with_llm.return_value = "Test summary response"
        
        result = await summarize(markdown_text="# Test Markdown\n\nThis is test content.")
        
        assert result == "Test summary response"
        mock_summarize_with_llm.assert_called_once_with(
            lecture_text="# Test Markdown\n\nThis is test content.",
            file=None,
            filename=None
        )
    
    @pytest.mark.asyncio
    @patch('services.summarizer.summarize_with_llm')
    async def test_summarize_with_file(self, mock_summarize_with_llm):
        """Test summarize function with file input"""
        mock_summarize_with_llm.return_value = "Test summary response"
        
        mock_file = Mock()
        
        result = await summarize(file=mock_file, filename="test.pdf")
        
        assert result == "Test summary response"
        mock_summarize_with_llm.assert_called_once_with(
            lecture_text=None,
            file=mock_file,
            filename="test.pdf"
        )
    
    @pytest.mark.asyncio
    @patch('services.summarizer.summarize_with_llm')
    async def test_summarize_with_all_parameters(self, mock_summarize_with_llm):
        """Test summarize function with all parameters"""
        mock_summarize_with_llm.return_value = "Test summary response"
        
        mock_file = Mock()
        
        result = await summarize(
            markdown_text="Test text",
            file=mock_file,
            filename="test.pdf"
        )
        
        assert result == "Test summary response"
        mock_summarize_with_llm.assert_called_once_with(
            lecture_text="Test text",
            file=mock_file,
            filename="test.pdf"
        )
    
    @pytest.mark.asyncio
    @patch('services.summarizer.summarize_with_llm')
    async def test_summarize_with_no_parameters(self, mock_summarize_with_llm):
        """Test summarize function with no parameters"""
        mock_summarize_with_llm.return_value = "Test summary response"
        
        result = await summarize()
        
        assert result == "Test summary response"
        mock_summarize_with_llm.assert_called_once_with(
            lecture_text=None,
            file=None,
            filename=None
        )