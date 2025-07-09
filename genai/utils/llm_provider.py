from dotenv import load_dotenv
import os
import requests
from typing import List, Any, Optional
from langchain.llms.base import LLM
from langchain.callbacks.manager import CallbackManagerForLLMRun

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
                timeout=180
            )
            response.raise_for_status()
            result = response.json()
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

def get_llm():
    return GenericLLM()
