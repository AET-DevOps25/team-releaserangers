import pytest
import os
from unittest.mock import Mock, patch
import sys
from utils.llm_provider import GenericLLM, FileCapableLLM, get_llm

# Add the parent directory to the path to import the application modules
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


class TestGenericLLM:
    """Test cases for GenericLLM class"""
    
    def test_init_with_env_vars(self):
        """Test LLM initialization with environment variables"""
        llm = GenericLLM()
        assert llm.api_url == "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite-preview-06-17:generateContent"
        assert llm.api_key == "test-key"  # pragma: allowlist secret
        assert llm.model_name == "gemini-2.5-flash-lite-preview-06-17"
        assert llm.backend == "google"
    
    def test_llm_type_property(self):
        """Test _llm_type property returns backend"""
        llm = GenericLLM()
        assert llm._llm_type == "google"
    
    @patch('requests.post')
    def test_call_google_backend_success(self, mock_post):
        """Test successful API call to Google backend"""
        # Mock response
        mock_response = Mock()
        mock_response.status_code = 200
        mock_response.json.return_value = {
        "candidates": [
            {
            "content": {
                "parts": [
                {
                    "text": "Test response from Google"
                }
                ],
                "role": "model"
            },
            "finishReason": "STOP",
            "index": 0
            }
        ],
        "usageMetadata": {
            "promptTokenCount": 8,
            "candidatesTokenCount": 8,
            "totalTokenCount": 16,
            "promptTokensDetails": [
            {
                "modality": "TEXT",
                "tokenCount": 8
            }
            ]
        },
        "modelVersion": "gemini-2.5-flash-lite-preview-06-17",
        "responseId": "test-id"
        }
        mock_response.raise_for_status.return_value = None
        mock_post.return_value = mock_response
        
        llm = GenericLLM()
        result = llm._call("Test prompt")
        
        assert result == "Test response from Google"
        mock_post.assert_called_once()
    
    @patch('requests.post')
    def test_call_http_error(self, mock_post):
        """Test HTTP error handling"""
        mock_response = Mock()
        mock_response.status_code = 500
        mock_response.raise_for_status.side_effect = Exception("HTTP 500 Error")
        mock_response.json.return_value = {"error": "Internal server error"}
        mock_post.return_value = mock_response
        
        llm = GenericLLM()
        
        with pytest.raises(Exception) as exc_info:
            llm._call("Test prompt")
        
        assert "HTTP 500 Error" in str(exc_info.value)
    
    def test_call_missing_api_key(self):
        """Test error when API key is missing"""
        with patch.dict(os.environ, {"LLM_API_KEY": ""}):
            llm = GenericLLM()
            
            with pytest.raises(Exception) as exc_info:
                llm._call("Test prompt")
            
            assert "API key not valid" in str(exc_info.value)
    
    def test_call_missing_api_url(self):
        """Test error when API URL is missing"""
        with patch.dict(os.environ, {"LLM_API_URL": ""}):
            llm = GenericLLM()
            
            with pytest.raises(Exception) as exc_info:
                llm._call("Test prompt")
            
            assert "API request failed: 400 Client Error:" in str(exc_info.value)


class TestFileCapableLLM:
    """Test cases for FileCapableLLM class"""
    
    def test_init_with_env_vars(self):
        """Test FileCapableLLM initialization"""
        llm = FileCapableLLM()
        assert llm.api_url == "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite-preview-06-17:generateContent"
        assert llm.api_key == "test-key"  # pragma: allowlist secret
        assert llm.model_name == "gemini-2.5-flash-lite-preview-06-17"
        assert llm.backend == "google"


class TestGetLLM:
    """Test cases for get_llm function"""
    
    def test_get_llm_file_parsing_true(self):
        """Test get_llm returns GenericLLM when fileParsing is True"""
        llm = get_llm(fileParsing=True)
        assert isinstance(llm, GenericLLM)
    
    def test_get_llm_file_parsing_false(self):
        """Test get_llm returns FileCapableLLM when fileParsing is False"""
        llm = get_llm(fileParsing=False)
        assert isinstance(llm, FileCapableLLM)
