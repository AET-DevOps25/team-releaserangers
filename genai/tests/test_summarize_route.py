import pytest
import os
import sys
import json
from unittest.mock import Mock, patch, AsyncMock
from io import BytesIO
from routes.summarize import summarize_pdf, clean_markdown

# Add the parent directory to the path to import the application modules
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


class TestSummarizeRoute:
    """Test cases for the summarize route"""
    
    @pytest.mark.asyncio
    @patch('routes.summarize.httpx.AsyncClient')
    @patch('routes.summarize.summarizer.summarize')
    @patch('routes.summarize.pdf_parser.extract_markdown_langchain')
    @patch.dict(os.environ, {"FILE_PARSING": "True", "COURSEMGMT_URL": "http://localhost:8081"})
    async def test_summarize_pdf_file_parsing_true(self, extract_markdown_langchain, mock_summarize, mock_client):
        """Test summarize_pdf with FILE_PARSING=True"""
        # Mock file
        mock_file = Mock()
        mock_file.filename = "test.pdf"
        mock_file.file = BytesIO(b"Test content")
        
        # Mock markdown extraction
        extract_markdown_langchain.return_value = "# Test Chapter\n\nThis is test content."
        
        # Mock summarizer
        mock_summary_json = {
            "chapter_title": "Test Chapter",
            "summary_markdown": "# Test Chapter\n\nSummary content.",
            "emoji": "📚"
        }
        mock_summarize.return_value = json.dumps(mock_summary_json)
        
        # Mock HTTP client
        mock_client_instance = Mock()
        mock_response = Mock()
        mock_response.status_code = 200
        mock_response.json.return_value = {"id": "chapter-123"}
        mock_client_instance.post = AsyncMock(return_value=mock_response)
        mock_client_instance.__aenter__ = AsyncMock(return_value=mock_client_instance)
        mock_client_instance.__aexit__ = AsyncMock(return_value=None)
        mock_client.return_value = mock_client_instance
        
        result = await summarize_pdf(
            courseId="course-123",
            existingChapterSummary="{}",
            files=[mock_file],
            token="test-token"
        )
        
        assert len(result) == 1
        assert result[0].chapter_title == "Test Chapter"
        assert result[0].summary_markdown == "# Test Chapter\n\nSummary content."
        assert result[0].emoji == "📚"
        assert result[0].source_file == "test.pdf"
        
        # Verify API call
        mock_client_instance.post.assert_called_once()
    
    @pytest.mark.asyncio
    @patch('routes.summarize.httpx.AsyncClient')
    @patch('routes.summarize.summarizer.summarize')
    @patch.dict(os.environ, {"FILE_PARSING": "False", "COURSEMGMT_URL": "http://localhost:8081"})
    async def test_summarize_pdf_file_parsing_false(self, mock_summarize, mock_client):
        """Test summarize_pdf with FILE_PARSING=False"""
        # Mock file
        mock_file = Mock()
        mock_file.filename = "test.pdf"
        mock_file.file = BytesIO(b"Test content")
        
        # Mock summarizer
        mock_summary_json = {
            "chapter_title": "Direct File Processing",
            "summary_markdown": "# Direct File Processing\n\nContent from file.",
            "emoji": "📄"
        }
        mock_summarize.return_value = json.dumps(mock_summary_json)
        
        # Mock HTTP client
        mock_client_instance = Mock()
        mock_response = Mock()
        mock_response.status_code = 200
        mock_response.json.return_value = {"id": "chapter-456"}
        mock_client_instance.post = AsyncMock(return_value=mock_response)
        mock_client_instance.__aenter__ = AsyncMock(return_value=mock_client_instance)
        mock_client_instance.__aexit__ = AsyncMock(return_value=None)
        mock_client.return_value = mock_client_instance
        
        result = await summarize_pdf(
            courseId="course-456",
            existingChapterSummary="{}",
            files=[mock_file],
            token="test-token"
        )
        
        assert len(result) == 1
        assert result[0].chapter_title == "Direct File Processing"
        assert result[0].summary_markdown == "# Direct File Processing\n\nContent from file."
        assert result[0].emoji == "📄"
        assert result[0].source_file == "test.pdf"
        
        # Verify API calls
        mock_summarize.assert_called_once_with(file=mock_file.file, filename="test.pdf")
        mock_client_instance.post.assert_called_once()
    
    @pytest.mark.asyncio
    @patch('routes.summarize.httpx.AsyncClient')
    @patch('routes.summarize.summarizer.summarize')
    @patch('routes.summarize.pdf_parser.extract_markdown_langchain')
    @patch.dict(os.environ, {"FILE_PARSING": "True", "COURSEMGMT_URL": "http://localhost:8081"})
    async def test_summarize_pdf_multiple_files(self, mock_extract_markdown, mock_summarize, mock_client):
        """Test summarize_pdf with multiple files"""
        # Mock files
        mock_file1 = Mock()
        mock_file1.filename = "test1.pdf"
        mock_file1.file = BytesIO(b"Test content 1")
        
        mock_file2 = Mock()
        mock_file2.filename = "test2.pdf"
        mock_file2.file = BytesIO(b"Test content 2")
        
        # Mock markdown extraction
        mock_extract_markdown.side_effect = [
            "# Chapter 1\n\nContent 1.",
            "# Chapter 2\n\nContent 2."
        ]
        
        # Mock summarizer
        mock_summarize.side_effect = [
            json.dumps({
                "chapter_title": "Chapter 1",
                "summary_markdown": "# Chapter 1\n\nSummary 1.",
                "emoji": "📚"
            }),
            json.dumps({
                "chapter_title": "Chapter 2",
                "summary_markdown": "# Chapter 2\n\nSummary 2.",
                "emoji": "📖"
            })
        ]
        
        # Mock HTTP client
        mock_client_instance = Mock()
        mock_response = Mock()
        mock_response.status_code = 200
        mock_response.json.return_value = {"id": "chapter-123"}
        mock_client_instance.post = AsyncMock(return_value=mock_response)
        mock_client_instance.__aenter__ = AsyncMock(return_value=mock_client_instance)
        mock_client_instance.__aexit__ = AsyncMock(return_value=None)
        mock_client.return_value = mock_client_instance
        
        result = await summarize_pdf(
            courseId="course-123",
            existingChapterSummary="{}",
            files=[mock_file1, mock_file2],
            token="test-token"
        )
        
        # assert len(result) == 2
        assert result[0].chapter_title == "Chapter 1"
        assert result[0].source_file == "test1.pdf"
        assert result[1].chapter_title == "Chapter 2"
        assert result[1].source_file == "test2.pdf"
        
        # Verify API calls
        assert mock_summarize.call_count == 2
        assert mock_client_instance.post.call_count == 2
    
    @pytest.mark.asyncio
    async def test_summarize_pdf_missing_token(self):
        """Test summarize_pdf with missing token"""
        mock_file = Mock()
        mock_file.filename = "test.pdf"
        
        with pytest.raises(Exception) as exc_info:
            await summarize_pdf(
                courseId="course-123",
                existingChapterSummary="{}",
                files=[mock_file],
                token=None
            )
        
        assert "Missing Cookie" in str(exc_info)
    
    @pytest.mark.asyncio
    @patch('routes.summarize.httpx.AsyncClient')
    @patch('routes.summarize.summarizer.summarize')
    @patch('routes.summarize.pdf_parser.extract_markdown_langchain')
    @patch.dict(os.environ, {"FILE_PARSING": "True", "COURSEMGMT_URL": "http://localhost:8081"})
    async def test_summarize_pdf_invalid_llm_response(self, mock_extract_markdown, mock_summarize, mock_client):
        """Test summarize_pdf with invalid LLM response"""
        # Mock file
        mock_file = Mock()
        mock_file.filename = "test.pdf"
        mock_file.file = BytesIO(b"Test content")
        
        # Mock markdown extraction
        mock_extract_markdown.return_value = "# Test Chapter"
        
        # Mock summarizer with invalid JSON
        mock_summarize.return_value = "invalid json response"
        
        with pytest.raises(Exception) as exc_info:
            await summarize_pdf(
                courseId="course-123",
                existingChapterSummary="{}",
                files=[mock_file],
                token="test-token"
            )
        
        assert "LLM returned invalid JSON" in str(exc_info)
    
    @pytest.mark.asyncio
    @patch('routes.summarize.httpx.AsyncClient')
    @patch('routes.summarize.summarizer.summarize')
    @patch('routes.summarize.pdf_parser.extract_markdown_langchain')
    @patch.dict(os.environ, {"FILE_PARSING": "True", "COURSEMGMT_URL": "http://localhost:8081"})
    async def test_summarize_pdf_backend_error(self, mock_extract_markdown, mock_summarize, mock_client):
        """Test summarize_pdf with backend service error"""
        # Mock file
        mock_file = Mock()
        mock_file.filename = "test.pdf"
        mock_file.file = BytesIO(b"Test content")
        
        # Mock markdown extraction
        mock_extract_markdown.return_value = "# Test Chapter"
        
        # Mock summarizer
        mock_summary_json = {
            "chapter_title": "Test Chapter",
            "summary_markdown": "# Test Chapter",
            "emoji": "📚"
        }
        mock_summarize.return_value = json.dumps(mock_summary_json)
        
        # Mock HTTP client with error
        mock_client_instance = Mock()
        mock_response = Mock()
        mock_response.status_code = 500
        mock_client_instance.post = AsyncMock(return_value=mock_response)
        mock_client_instance.__aenter__ = AsyncMock(return_value=mock_client_instance)
        mock_client_instance.__aexit__ = AsyncMock(return_value=None)
        mock_client.return_value = mock_client_instance
        
        # expect value
        with pytest.raises(Exception) as exc_info:
            await summarize_pdf(
                courseId="course-123",
                existingChapterSummary="{}",
                files=[mock_file],
                token="test-token"
            )
        
        assert "HTTPException" in str(exc_info)


class TestCleanMarkdown:
    """Test cases for clean_markdown function"""
    
    def test_clean_markdown_with_json_wrapper(self):
        """Test cleaning markdown with JSON code block wrapper"""
        input_str = '```json\n{"key": "value"}\n```'
        result = clean_markdown(input_str)
        assert result == '{"key": "value"}'
    
    def test_clean_markdown_without_wrapper(self):
        """Test cleaning markdown without wrapper"""
        input_str = '{"key": "value"}'
        result = clean_markdown(input_str)
        assert result == '{"key": "value"}'
    
    def test_clean_markdown_with_newlines_in_strings(self):
        """Test cleaning markdown with newlines in JSON strings"""
        input_str = '{"text": "Line 1\\nLine 2"}'
        result = clean_markdown(input_str)
        assert result == '{"text": "Line 1\\nLine 2"}'
    
    def test_clean_markdown_whitespace_handling(self):
        """Test cleaning markdown with extra whitespace"""
        input_str = '   ```json\n  {"key": "value"}  \n```   '
        result = clean_markdown(input_str)
        assert result == '{"key": "value"}'
    
    def test_clean_markdown_empty_string(self):
        """Test cleaning empty string"""
        input_str = ''
        result = clean_markdown(input_str)
        assert result == ''
    
    def test_clean_markdown_only_wrapper(self):
        """Test cleaning string with only wrapper"""
        input_str = '```json\n```'
        result = clean_markdown(input_str)
        assert result == ''
