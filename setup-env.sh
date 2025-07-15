#!/bin/bash

set -e

# Paths to env files
SERVER_ENV=".env"
SERVER_ENV_EXAMPLE=".env.example"
CLIENT_ENV="client/.env.local" # (deprecated, will not be used)
CLIENT_ENV_EXAMPLE="client/.env.example" # (deprecated, will not be used)
GENAI_ENV="genai/.env"
GENAI_ENV_EXAMPLE="genai/.env.example"

# Helper to print status
print_status() {
  echo -e "\033[1;34m$1\033[0m"
}

# Function to generate a random JWT secret
generate_jwt_secret() {
  openssl rand -hex 32
}

# Function to extract a variable from a file
grab_var() {
  local var="$1"
  local file="$2"
  if [ -f "$file" ]; then
    grep -E "^$var=" "$file" | cut -d'=' -f2- | tr -d '"' | tr -d '\r\n'
  fi
}

# Function to set a variable in a file (replace or add)
set_var() {
  local var="$1"
  local value="$2"
  local file="$3"
  if grep -q "^$var=" "$file"; then
    sed -i '' "s|^$var=.*$|$var=$value|" "$file"
  else
    echo "$var=$value" >> "$file"
  fi
}

# Prompt before overwriting a file
prompt_overwrite() {
  local file="$1"
  if [ -f "$file" ]; then
    read -p "$file already exists. Overwrite? (Y/N, default N): " answer
    answer=${answer:-N}
    if [[ ! "$answer" =~ ^[Yy]$ ]]; then
      print_status "Skipping overwrite of $file."
      return 1
    else
      print_status "Overwriting $file as requested."
    fi
  fi
  return 0
}

# Check if env files exist and prompt before continuing
prompt_overwrite "$GENAI_ENV"
prompt_overwrite "$SERVER_ENV"

# Always create new empty .env and genai/.env files
if [ -f "$SERVER_ENV" ]; then
  rm "$SERVER_ENV"
fi
if [ -f "$GENAI_ENV" ]; then
  rm "$GENAI_ENV"
fi
touch "$SERVER_ENV"
touch "$GENAI_ENV"

# --- JWT_SECRET setup ---
EXAMPLE_JWT_SECRET=$(grab_var "JWT_SECRET" "$SERVER_ENV_EXAMPLE")
if [ -n "$EXAMPLE_JWT_SECRET" ] && [ "$EXAMPLE_JWT_SECRET" != "<your_jwt_secret>" ]; then
  JWT_SECRET="$EXAMPLE_JWT_SECRET"
  print_status "Using JWT_SECRET from .env.example."
else
  JWT_SECRET=$(generate_jwt_secret)
  print_status "Generated new JWT_SECRET."
fi

echo "JWT_SECRET=$JWT_SECRET" >> "$SERVER_ENV"

# --- CLIENT_URL setup ---
CLIENT_URL=$(grab_var "CLIENT_URL" "$SERVER_ENV_EXAMPLE")
if [ -z "$CLIENT_URL" ]; then
  CLIENT_URL="http://localhost:3000"
fi
echo "CLIENT_URL=$CLIENT_URL" >> "$SERVER_ENV"

# --- LLM_API_KEY setup for genai/.env ---
EXAMPLE_LLM_API_KEY=$(grab_var "LLM_API_KEY" "$SERVER_ENV_EXAMPLE")
if [ -n "$EXAMPLE_LLM_API_KEY" ] && [ "$EXAMPLE_LLM_API_KEY" != "<your-api-key-here>" ]; then
  LLM_API_KEY="$EXAMPLE_LLM_API_KEY"
  print_status "Using LLM_API_KEY from .env.example."
else
  read -p "Enter your gemini LLM_API_KEY for GenAI service: " LLM_API_KEY
  while [ -z "$LLM_API_KEY" ]; do
    echo "LLM_API_KEY is required. Please enter your key: "
    read LLM_API_KEY
  done
fi
echo "LLM_API_KEY=$LLM_API_KEY" >> "$GENAI_ENV"

# --- Other GenAI variables from .env.example ---
for var in LLM_API_URL LLM_MODEL LLM_BACKEND COURSEMGMT_URL FILE_PARSING; do
  value=$(grab_var "$var" "$SERVER_ENV_EXAMPLE")
  if [ -n "$value" ]; then
    echo "$var=$value" >> "$GENAI_ENV"
    print_status "$var set in $GENAI_ENV."
  fi
done

print_status "All .env files are configured. Please review them for any additional settings."
