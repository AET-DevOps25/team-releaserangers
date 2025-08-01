from dotenv import load_dotenv
import os
import requests
from typing import List, Any, Optional
from langchain.llms.base import LLM
from langchain.callbacks.manager import CallbackManagerForLLMRun
import io
from google import genai

load_dotenv()

class GenericLLM(LLM):
    """
    Generic LangChain LLM wrapper configurable via environment variables.
    """
    api_url: str = os.getenv("LLM_API_URL")
    api_key: str = os.getenv("LLM_API_KEY")
    model_name: str = os.getenv("LLM_MODEL")
    backend: str = os.getenv("LLM_BACKEND")

    @property
    def _llm_type(self) -> str:
        return self.backend

    def _call(
        self,
        prompt: str,
        stop: Optional[List[str]] = None,
        run_manager: Optional[CallbackManagerForLLMRun] = None,
        **kwargs: Any,
    ) -> str:
        if not self.api_key:
            raise ValueError("LLM_API_KEY environment variable is required")
        if not self.api_url:
            raise ValueError("LLM_API_URL environment variable is required")
        
        if self.backend == "google":
            # Special handling for Google Gemini
            headers = {
                "X-goog-api-key": self.api_key,
                "Content-Type": "application/json",
            }
            payload = {
                "contents": [
                    {
                        "parts": [
                            {
                                "text": prompt
                            }
                        ]
                    }
                ]
            }
            # For Gemini, the API URL already contains the model
            api_url = self.api_url
        else:
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
            api_url = self.api_url

        try:
            response = requests.post(
                api_url,
                headers=headers,
                json=payload,
                timeout=180
            )
            response.raise_for_status()
            result = response.json()

            if self.backend == "google":
                if "candidates" in result and len(result["candidates"]) > 0:
                    return result["candidates"][0]["content"]["parts"][0]["text"].strip()
                else:
                    raise ValueError("Unexpected response format from Google API")
            else:
                if "choices" in result and len(result["choices"]) > 0:
                    return result["choices"][0]["message"]["content"].strip()
                else:
                    raise ValueError("Unexpected response format")
        except requests.HTTPError as e:
            try:
                error_detail = response.json()
            except Exception:
                error_detail = response.text
            raise Exception(f"API request failed: {str(e)}; Response: {error_detail}")
        except requests.RequestException as e:
            raise Exception(f"API request failed: {str(e)}")
        except (KeyError, IndexError, ValueError) as e:
            raise Exception(f"Failed to parse API response: {str(e)}")

class FileCapableLLM(LLM):
    """
    LLM wrapper that supports Google Gemini text + file (PDF) generation using the File API.
    """
    api_url: str = os.getenv("LLM_API_URL")
    api_key: str = os.getenv("LLM_API_KEY")
    model_name: str = os.getenv("LLM_MODEL")
    backend: str = os.getenv("LLM_BACKEND")

    @property
    def _llm_type(self) -> str:
        return self.backend

    def _call(
        self,
        prompt: str,
        file: Optional[io.BytesIO] = None,
        stop: Optional[List[str]] = None,
        run_manager: Optional[CallbackManagerForLLMRun] = None,
        **kwargs: Any,
    ) -> str:
        if self.backend != "google":
            raise NotImplementedError("This class only supports Google Gemini for file input.")
        
        if not self.api_key:
            raise ValueError("LLM_API_KEY environment variable is required")
        if not self.api_url:
            raise ValueError("LLM_API_URL environment variable is required")
        
        client = genai.Client(api_key=self.api_key)

        uploaded_file = client.files.upload(
            file=file,
            config={"mime_type": "application/pdf"}
        )
        
        response = client.models.generate_content(
            model=self.model_name,
            contents=[uploaded_file, prompt],
        )
        return response.text.strip()

def get_llm(fileParsing):
    if fileParsing:
        return GenericLLM()
    else: 
        return FileCapableLLM()
