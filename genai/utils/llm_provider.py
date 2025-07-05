from dotenv import load_dotenv
import os
import requests
from typing import List, Any, Optional
from langchain.llms.base import LLM
from langchain.callbacks.manager import CallbackManagerForLLMRun

load_dotenv() 

#LLM_BACKEND = "openwebui"
LLM_BACKEND = "perplexity"

# Environment configuration
CHAIR_API_KEY = os.getenv("CHAIR_API_KEY")
API_URL = os.getenv("CHAIR_API_URL")

PERPLEXITY_API_KEY = os.getenv("PERPLEXITY_API_KEY")
PERPLEXITY_API_URL = os.getenv("PERPLEXITY_API_URL")

class OpenWebUILLM(LLM):
    """
    Custom LangChain LLM wrapper for Open WebUI API.
    
    This class integrates the Open WebUI API with LangChain's LLM interface,
    allowing us to use the API in LangChain chains and pipelines.
    """
    
    api_url: str = API_URL
    api_key: str = CHAIR_API_KEY
    model_name: str = "llama3.3:latest"
    
    @property
    def _llm_type(self) -> str:
        return "open_webui"
    
    def _call(
        self,
        prompt: str,
        stop: Optional[List[str]] = None,
        run_manager: Optional[CallbackManagerForLLMRun] = None,
        **kwargs: Any,
    ) -> str:
        """
        Call the Open WebUI API to generate a response.
        
        Args:
            prompt: The input prompt to send to the model
            stop: Optional list of stop sequences
            run_manager: Optional callback manager for LangChain
            **kwargs: Additional keyword arguments
            
        Returns:
            The generated response text
            
        Raises:
            Exception: If API call fails
        """
        if not self.api_key:
            raise ValueError("CHAIR_API_KEY environment variable is required")
        
        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json",
        }
        
        # Build messages for chat completion
        messages = [
            {"role": "user", "content": prompt}
        ]
        
        payload = {
            "model": self.model_name,
            "messages": messages,
            "temperature": 0.5,
            "max_tokens": 100000,
        }
       
        try:
            response = requests.post(
                self.api_url,
                headers=headers,
                json=payload,
                timeout=180
            )
            response.raise_for_status()
            result = response.json()
            print(result)
            
            # Extract the response content
            if "choices" in result and len(result["choices"]) > 0:
                content = result["choices"][0]["message"]["content"]
                return content.strip()
            else:
                raise ValueError("Unexpected response format from API")
                
        except requests.RequestException as e:
            raise Exception(f"API request failed: {str(e)}")
        except (KeyError, IndexError, ValueError) as e:
            raise Exception(f"Failed to parse API response: {str(e)}")

class PerplexityLLM(LLM):
    api_url: str = PERPLEXITY_API_URL
    api_key: str = PERPLEXITY_API_KEY
    model_name: str = "sonar"

    @property
    def _llm_type(self) -> str:
        return "perplexity"

    def _call(
        self,
        prompt: str,
        stop: Optional[List[str]] = None,
        run_manager: Optional[CallbackManagerForLLMRun] = None,
        **kwargs: Any,
    ) -> str:
        if not self.api_key:
            raise ValueError("PERPLEXITY_API_KEY environment variable is required")

        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json",
        }

        messages = [
            {"role": "user", "content": prompt}
        ]

        payload = {
            "model": self.model_name,
            "messages": messages,
            "temperature": 0.5,
            "max_tokens": 100000,
        }

        try:
            response = requests.post(
                self.api_url,
                headers=headers,
                json=payload,
                timeout=60
            )
            response.raise_for_status()
            result = response.json()
            if "choices" in result and len(result["choices"]) > 0:
                return result["choices"][0]["message"]["content"].strip()
            else:
                raise ValueError("Unexpected response format")
        except requests.HTTPError as e:
            # Attempt to get more info from response body on 4xx/5xx errors
            try:
                error_detail = response.json()
            except Exception:
                error_detail = response.text
            raise Exception(f"API request failed: {str(e)}; Response: {error_detail}")
        except requests.RequestException as e:
            raise Exception(f"API request failed: {str(e)}")
        except (KeyError, IndexError, ValueError) as e:
            raise Exception(f"Failed to parse API response: {str(e)}")



def get_llm():
    if LLM_BACKEND == "openwebui":
        return OpenWebUILLM()
    elif LLM_BACKEND == "perplexity":
        return PerplexityLLM()
    else:
        raise ValueError(f"Unknown LLM_BACKEND: {LLM_BACKEND}")
