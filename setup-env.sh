#!/bin/bash

set -e

# Function to generate a random JWT secret
generate_jwt_secret() {
  openssl rand -hex 32
}

# Function to extract JWT_SECRET from a file
grab_jwt_secret() {
  local file="$1"
  if [ -f "$file" ]; then
    grep -E '^JWT_SECRET=' "$file" | cut -d'=' -f2- | tr -d '"' | tr -d '\r\n'
  fi
}

# Function to set JWT_SECRET in a file (replace or add)
set_jwt_secret() {
  local file="$1"
  local secret="$2"
  if grep -q '^JWT_SECRET=' "$file"; then
    sed -i '' "s|^JWT_SECRET=.*$|JWT_SECRET=$secret|" "$file"
  else
    echo "JWT_SECRET=$secret" >> "$file"
  fi
}

# Function to prompt before overwriting an existing file
prompt_overwrite() {
  local file="$1"
  if [ -f "$file" ]; then
    read -p "$file already exists. Overwrite? (Y/N, default N): " answer
    answer=${answer:-N}
    if [[ ! "$answer" =~ ^[Yy]$ ]]; then
      echo "Skipping overwrite of $file."
      return 1
    else
      echo "Overwriting $file as requested."
    fi
  fi
  return 0
}

# Setup root .env and client/.env.local
SERVER_ENV=".env"
SERVER_ENV_EXAMPLE=".env.example"
CLIENT_ENV="client/.env.local"
CLIENT_ENV_EXAMPLE="client/.env.example"

# Prompt and copy both files from example if missing or if user confirms overwrite
if [ ! -f "$SERVER_ENV" ] && [ ! -f "$CLIENT_ENV" ]; then
  cp "$SERVER_ENV_EXAMPLE" "$SERVER_ENV"
  cp "$CLIENT_ENV_EXAMPLE" "$CLIENT_ENV"
elif [ ! -f "$SERVER_ENV" ]; then
  cp "$SERVER_ENV_EXAMPLE" "$SERVER_ENV"
elif [ ! -f "$CLIENT_ENV" ]; then
  cp "$CLIENT_ENV_EXAMPLE" "$CLIENT_ENV"
else
  # Both files exist, prompt once for both
  read -p "$SERVER_ENV and $CLIENT_ENV already exist. Overwrite both? (y/N, default N): " answer
  answer=${answer:-N}
  if [[ "$answer" =~ ^[Yy]$ ]]; then
    echo "Overwriting $SERVER_ENV and $CLIENT_ENV as requested."
    cp "$SERVER_ENV_EXAMPLE" "$SERVER_ENV"
    echo "$SERVER_ENV overwritten from $SERVER_ENV_EXAMPLE."
    cp "$CLIENT_ENV_EXAMPLE" "$CLIENT_ENV"
    echo "$CLIENT_ENV overwritten from $CLIENT_ENV_EXAMPLE."
  else
    echo "Skipping overwrite of $SERVER_ENV and $CLIENT_ENV."
  fi
fi

# Grab secrets from both files
SERVER_SECRET=$(grab_jwt_secret "$SERVER_ENV")
CLIENT_SECRET=$(grab_jwt_secret "$CLIENT_ENV")

# Determine which secret to use, or generate a new one
if [[ -n "$SERVER_SECRET" && "$SERVER_SECRET" != "<your_jwt_secret>" && "$SERVER_SECRET" != "put_the_same_jwt_secret_as_in_server_applications_properties" ]]; then
  JWT_SECRET="$SERVER_SECRET"
elif [[ -n "$CLIENT_SECRET" && "$CLIENT_SECRET" != "<your_jwt_secret>" && "$CLIENT_SECRET" != "put_the_same_jwt_secret_as_in_server_applications_properties" ]]; then
  JWT_SECRET="$CLIENT_SECRET"
else
  JWT_SECRET=$(generate_jwt_secret)
fi

# Set the same JWT_SECRET in both files
set_jwt_secret "$SERVER_ENV" "$JWT_SECRET"
set_jwt_secret "$CLIENT_ENV" "$JWT_SECRET"

echo "JWT_SECRET synchronized in $SERVER_ENV and $CLIENT_ENV."
echo "All .env files are configured. Please review them for any additional settings."
