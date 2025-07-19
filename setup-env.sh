#!/bin/bash

set -e

# Paths to env files
SERVER_ENV=".env"
SERVER_ENV_EXAMPLE=".env.example"
GENAI_ENV="genai/.env"

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

# Prompt for overwriting both files first
GENAI_OVERWRITE=1
SERVER_OVERWRITE=1
if [ -f "$GENAI_ENV" ]; then
  read -p "$GENAI_ENV already exists. Overwrite? (Y/N, default N): " genai_answer
  genai_answer=${genai_answer:-N}
  if [[ "$genai_answer" =~ ^[Yy]$ ]]; then
    print_status "Overwriting $GENAI_ENV as requested."
    GENAI_OVERWRITE=0
  else
    print_status "Skipping overwrite of $GENAI_ENV."
  fi
else
  GENAI_OVERWRITE=0
fi
if [ -f "$SERVER_ENV" ]; then
  read -p "$SERVER_ENV already exists. Overwrite? (Y/N, default N): " server_answer
  server_answer=${server_answer:-N}
  if [[ "$server_answer" =~ ^[Yy]$ ]]; then
    print_status "Overwriting $SERVER_ENV as requested."
    SERVER_OVERWRITE=0
  else
    print_status "Skipping overwrite of $SERVER_ENV."
  fi
else
  SERVER_OVERWRITE=0
fi

# Only overwrite .env if allowed
if [ $SERVER_OVERWRITE -eq 0 ]; then
  OLD_JWT_SECRET=""
  if [ -f "$SERVER_ENV" ]; then
    OLD_JWT_SECRET=$(grab_var "JWT_SECRET" "$SERVER_ENV")
    rm "$SERVER_ENV"
  fi
  touch "$SERVER_ENV"

  # --- JWT_SECRET setup ---
  EXAMPLE_JWT_SECRET=$(grab_var "JWT_SECRET" "$SERVER_ENV_EXAMPLE")
  if [ -n "$OLD_JWT_SECRET" ]; then
    echo "Existing JWT_SECRET found in .env: $OLD_JWT_SECRET"
    read -p "Do you want to generate a new JWT_SECRET? (Y/N, default N): " jwt_answer
    jwt_answer=${jwt_answer:-N}
    if [[ "$jwt_answer" =~ ^[Yy]$ ]]; then
      JWT_SECRET=$(generate_jwt_secret)
      print_status "Generated new JWT_SECRET."
    else
      JWT_SECRET="$OLD_JWT_SECRET"
      print_status "Keeping existing JWT_SECRET."
    fi
  elif [ -n "$EXAMPLE_JWT_SECRET" ] && [ "$EXAMPLE_JWT_SECRET" != "<your_jwt_secret>" ]; then
    JWT_SECRET="$EXAMPLE_JWT_SECRET"
    print_status "Using JWT_SECRET from .env.example."
  else
    JWT_SECRET=$(generate_jwt_secret)
    print_status "Generated new JWT_SECRET."
  fi
  echo "JWT_SECRET=$JWT_SECRET" >> "$SERVER_ENV"
  sed -i '' "s/^JWT_SECRET=.*/JWT_SECRET=$JWT_SECRET/" "$SERVER_ENV_EXAMPLE"

  # --- CLIENT_URL setup ---
  CLIENT_URL=$(grab_var "CLIENT_URL" "$SERVER_ENV_EXAMPLE")
  if [ -z "$CLIENT_URL" ]; then
    CLIENT_URL="http://localhost:3000"
  fi
  echo "CLIENT_URL=$CLIENT_URL" >> "$SERVER_ENV"
fi

# Only overwrite genai/.env if allowed
if [ $GENAI_OVERWRITE -eq 0 ]; then
  if [ -f "$GENAI_ENV" ]; then
    rm "$GENAI_ENV"
  fi
  touch "$GENAI_ENV"

  # --- LLM_API_KEY setup for genai/.env ---
  EXAMPLE_LLM_API_KEY=$(grab_var "LLM_API_KEY" "$SERVER_ENV_EXAMPLE")
  if [ -n "$EXAMPLE_LLM_API_KEY" ] && [ "$EXAMPLE_LLM_API_KEY" != "<your-api-key-here>" ]; then
    LLM_API_KEY="$EXAMPLE_LLM_API_KEY"
    print_status "Using LLM_API_KEY from .env.example."
  else
    read -p "Enter your LLM_API_KEY (gemini key in the default setup) for GenAI service: " LLM_API_KEY
    while [ -z "$LLM_API_KEY" ]; do
      echo "LLM_API_KEY is required. Please enter your key: "
      read LLM_API_KEY
    done
  fi
  echo "LLM_API_KEY=$LLM_API_KEY" >> "$GENAI_ENV"
  sed -i '' "s/^LLM_API_KEY=.*/LLM_API_KEY=$LLM_API_KEY/" "$SERVER_ENV_EXAMPLE"
  # --- Other GenAI variables from .env.example ---
  for var in LLM_API_URL LLM_MODEL LLM_BACKEND COURSEMGMT_URL FILE_PARSING; do
    value=$(grab_var "$var" "$SERVER_ENV_EXAMPLE")
    if [ -n "$value" ]; then
      echo "$var=$value" >> "$GENAI_ENV"
      print_status "$var set in $GENAI_ENV."
    fi
  done
fi

# Summary
if [ $SERVER_OVERWRITE -eq 0 ]; then
  print_status "$SERVER_ENV was created/overwritten."
else
  print_status "$SERVER_ENV was not changed."
fi
if [ $GENAI_OVERWRITE -eq 0 ]; then
  print_status "$GENAI_ENV was created/overwritten."
else
  print_status "$GENAI_ENV was not changed."
fi

print_status "Setup complete."
print_status "All .env files are configured. Please review them for any additional settings."
print_status "You can find the server .env at: './$SERVER_ENV' and the GenAI .env at: './$GENAI_ENV'"
