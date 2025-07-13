#!/bin/bash

set -e

# Paths to env files
SERVER_ENV=".env"
SERVER_ENV_EXAMPLE=".env.example"
CLIENT_ENV="client/.env.local"
CLIENT_ENV_EXAMPLE="client/.env.example"
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
prompt_overwrite "$CLIENT_ENV"
prompt_overwrite "$SERVER_ENV"

# Ensure .env files exist, copy from example if missing
if [ ! -f "$SERVER_ENV" ]; then
  print_status "Creating $SERVER_ENV from $SERVER_ENV_EXAMPLE."
  cp "$SERVER_ENV_EXAMPLE" "$SERVER_ENV"
fi
if [ ! -f "$CLIENT_ENV" ]; then
  print_status "Creating $CLIENT_ENV from $CLIENT_ENV_EXAMPLE."
  cp "$CLIENT_ENV_EXAMPLE" "$CLIENT_ENV"
fi
if [ ! -f "$GENAI_ENV" ]; then
  print_status "Creating $GENAI_ENV."
  touch "$GENAI_ENV"
fi

# --- JWT_SECRET setup ---
EXAMPLE_JWT_SECRET=$(grab_var "JWT_SECRET" "$SERVER_ENV_EXAMPLE")
SERVER_SECRET=$(grab_var "JWT_SECRET" "$SERVER_ENV")
CLIENT_SECRET=$(grab_var "JWT_SECRET" "$CLIENT_ENV")

if [[ -n "$SERVER_SECRET" && "$SERVER_SECRET" != "$EXAMPLE_JWT_SECRET" ]]; then
  print_status "Current JWT_SECRET in $SERVER_ENV: $SERVER_SECRET"
  prompt_overwrite "$SERVER_ENV"
  if [ $? -eq 0 ]; then
    JWT_SECRET=$(generate_jwt_secret)
    set_var "JWT_SECRET" "$JWT_SECRET" "$SERVER_ENV"
    set_var "JWT_SECRET" "$JWT_SECRET" "$CLIENT_ENV"
    print_status "New JWT_SECRET generated and set in $SERVER_ENV and $CLIENT_ENV."
  else
    JWT_SECRET="$SERVER_SECRET"
    set_var "JWT_SECRET" "$JWT_SECRET" "$CLIENT_ENV"
    print_status "JWT_SECRET kept and synchronized in $CLIENT_ENV."
  fi
else
  JWT_SECRET=$(generate_jwt_secret)
  set_var "JWT_SECRET" "$JWT_SECRET" "$SERVER_ENV"
  set_var "JWT_SECRET" "$JWT_SECRET" "$CLIENT_ENV"
  print_status "JWT_SECRET generated and set in $SERVER_ENV and $CLIENT_ENV."
fi

# --- NEXT_PUBLIC_API_URL setup for client/.env.local ---
NEXT_PUBLIC_API_URL=$(grab_var "NEXT_PUBLIC_API_URL" "$SERVER_ENV_EXAMPLE")
if [ -z "$NEXT_PUBLIC_API_URL" ]; then
  NEXT_PUBLIC_API_URL="http://localhost"
fi
set_var "NEXT_PUBLIC_API_URL" "$NEXT_PUBLIC_API_URL" "$CLIENT_ENV"
print_status "NEXT_PUBLIC_API_URL set in $CLIENT_ENV."

# --- LLM_API_KEY setup for genai/.env ---
read -p "Enter your gemini LLM_API_KEY for GenAI service: " LLM_API_KEY
while [ -z "$LLM_API_KEY" ]; do
  echo "LLM_API_KEY is required. Please enter your key: "
  read LLM_API_KEY
done
set_var "LLM_API_KEY" "$LLM_API_KEY" "$GENAI_ENV"
print_status "LLM_API_KEY set in $GENAI_ENV."

# --- Other GenAI variables from .env.example ---
for var in LLM_API_URL LLM_MODEL LLM_BACKEND COURSEMGMT_URL; do
  value=$(grab_var "$var" "$SERVER_ENV_EXAMPLE")
  if [ -n "$value" ]; then
    set_var "$var" "$value" "$GENAI_ENV"
    print_status "$var set in $GENAI_ENV."
  fi
done

print_status "All .env files are configured. Please review them for any additional settings."
