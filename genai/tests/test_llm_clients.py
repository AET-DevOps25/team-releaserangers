import os
import json
import sys
from unittest.mock import Mock, patch
import pytest

# Add the parent directory to the path to import the application modules
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from utils.llm_clients import summarize_with_llm


class TestSummarizeWithLLM:
    """Test cases for summarize_with_llm function"""
    
    @pytest.mark.asyncio
    @patch('utils.llm_clients.get_llm')
    async def test_summarize_with_file(self, mock_get_llm):
        """Test summarization with file input"""
        # Mock LLM
        mock_llm = Mock()
        mock_llm.model_name = "test-model"
        mock_llm._call.return_value = json.dumps({
            "chapter_title": "Test Chapter",
            "summary_markdown": "# Test Summary\n\nThis is a test summary.",
            "emoji": "📚"
        })
        mock_get_llm.return_value = mock_llm
        
        # Create mock file
        mock_file = Mock()
        mock_file.read = Mock(return_value=b"Test file content")
        
        result = await summarize_with_llm(file=mock_file, filename="test.pdf")
        
        assert "Test Chapter" in result
        assert "Test Summary" in result
        mock_get_llm.assert_called_once()
        mock_llm._call.assert_called_once()
    
    @pytest.mark.asyncio
    @patch('utils.llm_clients.get_llm')
    @patch.dict(os.environ, {"FILE_PARSING": "True"})
    async def test_summarize_with_text_model_name_fallback(self, mock_get_llm):
        """Test model name fallback when model_name attribute is not available"""
        # Mock LLM without model_name but with model attribute
        mock_llm = Mock()
        mock_llm.model_name = None
        mock_llm.model = "fallback-model"
        mock_get_llm.return_value = mock_llm
        
        # Mock the chain invoke
        mock_chain = Mock()
        mock_chain.invoke.return_value = json.dumps({
            "chapter_title": "Test Chapter",
            "summary_markdown": "# Test Summary",
            "emoji": "📚"
        })
        
        with patch('utils.llm_clients.PromptTemplate') as mock_prompt:
            mock_prompt.return_value.__or__ = Mock(return_value=mock_chain)
            
            # Create mock file to avoid AttributeError
            mock_file = Mock()
            mock_file.read = Mock(return_value=b"Test content")
            
            result = await summarize_with_llm(lecture_text="Test content", file=mock_file)
            
            assert result is not None
            mock_get_llm.assert_called_once()
    
    @pytest.mark.asyncio
    @patch('utils.llm_clients.get_llm')
    @patch.dict(os.environ, {"FILE_PARSING": "True"})
    async def test_summarize_with_text_unknown_model(self, mock_get_llm):
        """Test model name fallback to 'Unknown model' when no model info available"""
        # Mock LLM without model_name or model attributes
        mock_llm = Mock()
        del mock_llm.model_name
        del mock_llm.model
        mock_get_llm.return_value = mock_llm
        
        # Mock the chain invoke
        mock_chain = Mock()
        mock_chain.invoke.return_value = json.dumps({
            "chapter_title": "Test Chapter",
            "summary_markdown": "# Test Summary",
            "emoji": "📚"
        })
        
        with patch('utils.llm_clients.PromptTemplate') as mock_prompt:
            mock_prompt.return_value.__or__ = Mock(return_value=mock_chain)
            
            # Create mock file to avoid AttributeError
            mock_file = Mock()
            mock_file.read = Mock(return_value=b"Test content")
            
            result = await summarize_with_llm(lecture_text="Test content", file=mock_file, filename="test.pdf")
            
            assert result is not None
            mock_get_llm.assert_called_once()
    
    @pytest.mark.asyncio
    @patch('utils.llm_clients.get_llm')
    @patch.dict(os.environ, {"FILE_PARSING": "False"})
    async def test_summarize_with_file_and_filename(self, mock_get_llm):
        """Test summarization with both file and filename parameters"""
        # Mock LLM
        mock_llm = Mock()
        mock_llm.model_name = "test-model"
        mock_llm._call.return_value = json.dumps({
            "chapter_title": "Test Chapter",
            "summary_markdown": "# Test Summary",
            "emoji": "📚"
        })
        mock_get_llm.return_value = mock_llm
        
        # Create mock file
        mock_file = Mock()
        mock_file.read = Mock(return_value=b"Test file content")
        
        result = await summarize_with_llm(
            file=mock_file, 
            filename="test_document.pdf"
        )
        
        assert "Test Chapter" in result
        mock_get_llm.assert_called_once_with(fileParsing=False)
        
        # Verify that the _call method was called with file and prompt
        call_args = mock_llm._call.call_args
        assert 'file' in call_args.kwargs
        assert 'prompt' in call_args.kwargs