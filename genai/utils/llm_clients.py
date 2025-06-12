import os
import requests
from typing import List, Any, Optional
from langchain.llms.base import LLM
from langchain.callbacks.manager import CallbackManagerForLLMRun
from langchain_core.prompts import PromptTemplate



# Environment configuration
CHAIR_API_KEY = os.getenv("CHAIR_API_KEY")
print(CHAIR_API_KEY)
API_URL = "https://gpu.aet.cit.tum.de/api/chat/completions"

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


async def summarize_with_llm(lecture_content: str) -> str:
   
    # Initialize the LLM
    llm = OpenWebUILLM()

    # Create the prompt template
    summary_prompt = PromptTemplate(
    input_variables=["lecture_text"],
    template="""
            You are an academic summarization assistant. Your task is to process a student's raw lecture notes and generate:
            1. A concise and descriptive **chapter title** (max 10 words).
            2. A suitable **emoji** representing the topic (academic, not playful).
            3. A clean, structured, exhaustive **Markdown-formatted summary** of the lecture notes.

            Strict requirements:
            - **Use only the information contained in the input text.**
            - **Do not add external knowledge or inferred content.**
            - **Do not hallucinate or make assumptions.**
            - Avoid redundancy; keep it clear and comprehensive.
            - The summary should **accurately reflect the structure and depth** of the source material.
            - Avoid redundant information and unnecessary repetition.
            - The final output of the summary must be formatted in **Markdown**, using appropriate structural elements:
                - `#` for main topics
                - `##`, `###` for subtopics
                - Bullet points or numbered lists where appropriate
                - Code blocks, formulas, or other notation if they appear in the notes
            - The title must summarize the central topic.
            - Pick a relevant emoji: e.g., 📘, 🧠, 📐, 💻, 🧪 — avoid informal or irrelevant ones.
            - Return a valid JSON object, with all newlines inside string values escaped as \\n

            Input Lecture Notes:
            --------------------
            {lecture_text}
            --------------------

            Now, generate a clear, well-organized **Markdown-formatted** summary based on the content above. The output should be suitable for exam preparation.
            Respond in the following **JSON format**:
            {{
            "chapter_title": "Generated title here",
            "summary_markdown": "Markdown summary here",
            "emoji": "🎓",
            }}
            """
            )
    
    # Create the chain using the new RunnableSequence
    summary_chain = summary_prompt | llm

    # Use LangChain to create the summary
    summary = summary_chain.invoke(lecture_content)
    return summary
