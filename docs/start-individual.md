# Start Individual Services Guide

[← Back to Main README](../README.md)

### Client Setup

- create a `.env` file in the root directory of the project
- add the following environment variables:

  ```bash
  JWT_SECRET=<your_jwt_secret>
  ```

- for local development, create a `.env.local` file in the `client` directory and add the following environment variables:

  ```bash
  JWT_SECRET=<your_jwt_secret>
  NEXT_PUBLIC_API_URL="http://localhost"
  ```

  Attention: The JWT secret must be the same in both `.env`, `.env.local` and `authentication-service/src/main/resources/application.properties` files.

### Server Setup

- The authentication service uses the [dotenv-java](https://github.com/cdimascio/dotenv-java) library to automatically load environment variables from your `.env` file.
- Ensure you have a `.env` file in the root of your project with the following content:

  ```bash
  JWT_SECRET=<your_jwt_secret>
  ```

  You can orient yourself by looking at how the `.env.example` file looks like in the project root.

- The `application.properties` file in `server/authentication-service/src/main/resources/` uses a placeholder to read the secret:

  ```bash
  jwt.secret=${JWT_SECRET}
  ```

- You do not need to manually export environment variables. Simply run the authentication service as usual (e.g., `./mvnw spring-boot:run`), and the secret will be loaded automatically.

  **Note:** The JWT secret must be identical in `.env`, `.env.local` (for the client), and available to the authentication service for authentication to work correctly.

### LLM Service Setup

Make sure to create a .env file from the .env.example and add your API Key.

1. Navigate to the <code>genai</code> directory:

   ```bash
   cd genai
   ```

2. Install Dependencies:
   ```bash
   python3 -m venv .venv
   source .venv/bin/activate
   pip3 install -r requirements.txt
   ```

## Running the Application

### Start the Database

You can start the database using Docker Compose:

```bash
docker compose up postgres-db
```

### Start the Client

From the project root, run:

```bash
cd client
pnpm install
pnpm dev
```

### Start the Server

To start the microservices individually, repeat in their respective directories (e.g., authentication-service, coursemgmt-service, upload-service):

```bash
cd authentication-service
./mvnw spring-boot:run

cd coursemgmt-service
./mvnw spring-boot:run

cd upload-service
./mvnw spring-boot:run
```

### Start the LLM Service

- Using uvicorn directly:
  ```bash
  cd genai
  uvicorn main:app --host 0.0.0.0 --port 8084
  ```
- Using python3:
  ```bash
  cd genai
  python3 main.py
  ```
- Using Docker:
  ```bash
  cd genai
  docker build -t llm .
  docker run --env-file .env -p 8084:8084 llm
  ```