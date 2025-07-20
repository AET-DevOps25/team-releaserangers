import pytest
import os
import sys
from unittest.mock import Mock, patch, AsyncMock
from services.pdf_parser import extract_markdown_langchain, extract_markdown

# Add the parent directory to the path to import the application modules
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


class TestPDFParser:
    """Test cases for PDF parser service"""
    
    @pytest.mark.asyncio
    @patch('services.pdf_parser.DoclingLoader')
    @patch('services.pdf_parser.DocumentConverter')
    @patch('tempfile.NamedTemporaryFile')
    async def test_extract_markdown_langchain_success(self, mock_tempfile, mock_converter, mock_loader):
        """Test successful markdown extraction using LangChain"""
        # Mock UploadFile
        mock_upload_file = Mock()
        mock_upload_file.filename = "test.pdf"
        mock_upload_file.read = AsyncMock(return_value=b"Test PDF content")
        
        # Mock temporary file
        mock_temp = Mock()
        mock_temp.name = "/tmp/test.pdf"
        mock_temp.__enter__ = Mock(return_value=mock_temp)
        mock_temp.__exit__ = Mock(return_value=None)
        mock_tempfile.return_value = mock_temp
        
        # Mock DoclingLoader
        mock_loader_instance = Mock()
        mock_doc1 = Mock()
        mock_doc1.page_content = "# Chapter 1\n\nThis is the first chapter."
        mock_doc2 = Mock()
        mock_doc2.page_content = "# Chapter 2\n\nThis is the second chapter."
        mock_loader_instance.load.return_value = [mock_doc1, mock_doc2]
        mock_loader.return_value = mock_loader_instance
        
        # Mock DocumentConverter
        mock_converter.return_value = Mock()
        
        result = await extract_markdown_langchain(mock_upload_file)
        
        expected_result = "# Chapter 1\n\nThis is the first chapter.\n\n# Chapter 2\n\nThis is the second chapter."
        assert result == expected_result
        
        # Verify that the temporary file was created and used
        mock_tempfile.assert_called_once()
        mock_loader.assert_called_once()
        mock_loader_instance.load.assert_called_once()
    
    @pytest.mark.asyncio
    @patch('services.pdf_parser.DoclingLoader')
    @patch('services.pdf_parser.DocumentConverter')
    @patch('tempfile.NamedTemporaryFile')
    async def test_extract_markdown_langchain_empty_result(self, mock_tempfile, mock_converter, mock_loader):
        """Test markdown extraction with empty result"""
        # Mock UploadFile
        mock_upload_file = Mock()
        mock_upload_file.filename = "empty.pdf"
        mock_upload_file.read = AsyncMock(return_value=b"Empty PDF content")
        
        # Mock temporary file
        mock_temp = Mock()
        mock_temp.name = "/tmp/empty.pdf"
        mock_temp.__enter__ = Mock(return_value=mock_temp)
        mock_temp.__exit__ = Mock(return_value=None)
        mock_tempfile.return_value = mock_temp
        
        # Mock DoclingLoader with empty result
        mock_loader_instance = Mock()
        mock_loader_instance.load.return_value = []
        mock_loader.return_value = mock_loader_instance
        
        result = await extract_markdown_langchain(mock_upload_file)
        
        assert result == ""
    
    @pytest.mark.asyncio
    @patch('services.pdf_parser.DoclingLoader')
    @patch('services.pdf_parser.DocumentConverter')
    @patch('tempfile.NamedTemporaryFile')
    async def test_extract_markdown_langchain_no_filename_extension(self, mock_tempfile, mock_converter, mock_loader):
        """Test markdown extraction with file without extension"""
        # Mock UploadFile without extension
        mock_upload_file = Mock()
        mock_upload_file.filename = "testfile"
        mock_upload_file.read = AsyncMock(return_value=b"Test content")
        
        # Mock temporary file
        mock_temp = Mock()
        mock_temp.name = "/tmp/testfile.pdf"
        mock_temp.__enter__ = Mock(return_value=mock_temp)
        mock_temp.__exit__ = Mock(return_value=None)
        mock_tempfile.return_value = mock_temp
        
        # Mock DoclingLoader
        mock_loader_instance = Mock()
        mock_doc = Mock()
        mock_doc.page_content = "Test content"
        mock_loader_instance.load.return_value = [mock_doc]
        mock_loader.return_value = mock_loader_instance
        
        result = await extract_markdown_langchain(mock_upload_file)
        
        assert result == "Test content"
        # Verify that .pdf was used as default suffix
        mock_tempfile.assert_called_once_with(suffix=".pdf", delete=False)
    
    @pytest.mark.asyncio
    @patch('services.pdf_parser.DocumentConverter')
    async def test_extract_markdown_direct_conversion(self, mock_converter):
        """Test direct markdown extraction (currently not used function)"""
        # Mock UploadFile
        mock_upload_file = Mock()
        mock_upload_file.filename = "test.pdf"
        mock_upload_file.read = AsyncMock(return_value=b"Test PDF content")
        
        # Mock DocumentConverter
        mock_converter_instance = Mock()
        mock_result = Mock()
        mock_document = Mock()
        mock_document.export_to_markdown.return_value = "# Test Document\n\nThis is test content."
        mock_result.document = mock_document
        mock_converter_instance.convert.return_value = mock_result
        mock_converter.return_value = mock_converter_instance
        
        result = await extract_markdown(mock_upload_file)
        
        assert result == "# Test Document\n\nThis is test content."
        mock_converter_instance.convert.assert_called_once()
        mock_document.export_to_markdown.assert_called_once()
    
    @pytest.mark.asyncio
    @patch('services.pdf_parser.DoclingLoader')
    @patch('services.pdf_parser.DocumentConverter')
    @patch('tempfile.NamedTemporaryFile')
    async def test_extract_markdown_langchain_with_different_extension(self, mock_tempfile, mock_converter, mock_loader):
        """Test markdown extraction with different file extension"""
        # Mock UploadFile with .docx extension
        mock_upload_file = Mock()
        mock_upload_file.filename = "test.docx"
        mock_upload_file.read = AsyncMock(return_value=b"Test document content")
        
        # Mock temporary file
        mock_temp = Mock()
        mock_temp.name = "/tmp/test.docx"
        mock_temp.__enter__ = Mock(return_value=mock_temp)
        mock_temp.__exit__ = Mock(return_value=None)
        mock_tempfile.return_value = mock_temp
        
        # Mock DoclingLoader
        mock_loader_instance = Mock()
        mock_doc = Mock()
        mock_doc.page_content = "Document content"
        mock_loader_instance.load.return_value = [mock_doc]
        mock_loader.return_value = mock_loader_instance
        
        result = await extract_markdown_langchain(mock_upload_file)
        
        assert result == "Document content"
        # Verify that .docx suffix was used
        mock_tempfile.assert_called_once_with(suffix=".docx", delete=False)
