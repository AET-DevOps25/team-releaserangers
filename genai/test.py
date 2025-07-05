from dotenv import load_dotenv
import os
from langchain_anthropic import ChatAnthropic

# Load .env file
load_dotenv()

print(repr(os.getenv("ANTHROPIC_API_KEY")))

ANTHROPIC_API_KEY = os.getenv("ANTHROPIC_API_KEY")
if not ANTHROPIC_API_KEY:
    raise ValueError("ANTHROPIC_API_KEY is not set")

# Instantiate the Anthropic chat model
chat = ChatAnthropic(
    model="claude-3-opus-20240229",
    temperature=0.2,
    max_tokens=512
)

# Example prompt
prompt = "Hello Claude, please confirm you are working."

# Actually call the model
response = chat.invoke(prompt)

print("Response from Claude:")
print(response.content)
