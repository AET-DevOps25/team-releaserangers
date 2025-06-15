# DevOps 2025 - Team ReleaseRangers

This is a university project designed to enhance students learning experience by leveraging GenAI to create summaries of lecture material throughout the semester. We want to help students optimize their study time by automating the summarization process, allowing them to focus on understanding and applying rather than summarizing.

---

## Our Team

This project is maintained by:

- **Florian Charrot (FC)**
- **Jonathan Müller (JM)**
- **Luis Leutbecher (LL)**

---

## Student Responsibilities

| Week | Title                              | Student  | Description | Status | Impediments | Promises |
| ---- | ---------------------------------- | -------- | ----------- | ------ | ----------- | -------- |
| CW19 | Draft Problem Statement            | Everyone |             |        |             |          |
| CW20 | Create UML Models, Initial Backlog | Everyone |             |        |             |          |
| CW21 |                                    |          |             |        |             |          |
| CW22 |                                    |          |             |        |             |          |
| CW23 |                                    |          |             |        |             |          |

## Subsystem Ownership

- Subsystem1: ..
- Subsystem2: ..
- Subsystem3: ..
- ...

---

## Key Features

- **Upload all lecture material at a single place**
- **Structure relevant information into learning chapters**
- **Keep uploading whenever you receive new material**

---

## Project Overview

Our application helps students to study efficient by leveraging LLM generated smart summaries of their lecture material. Our vision is to create one single place where one can get a summarized overview of the lecture material needed for exam preparation. We want to enable students to easily add new content throughout the semester which constantly gets summarized to always provide the student with an up to date overview of the current course content.

---
## Setup Instructions

### Clone the Repository

### Client Setup
- create a `.env` file in the root directory of the project
- add the following environment variables:
  ```bash
  JWT_SECRET=<your_jwt_secret>
  ```

- for local development, create a `.env.local` file in the `client` directory and add the following environment variables:
  ```bash
  JWT_SECRET=<your_jwt_secret>
  ```

  Attention: The JWT secret must be the same in both `.env`, `.env.local` and `authentication-service/src/main/resources/application.properties` files.

### Server Setup

### LLM Service Setup
Make sure to create a .env file from the .env.example and add your API Key.
1. Navigate to the <code>genai</code> directory:
   
   ```bash
   cd genai
2. Install Dependencies:
   ````bash
   python3 -m venv .venv
   source .venv/bin/activate
   pip3 install -r requirements.txt
## Running the Application

### Start the Database

### Start the Client

### Start the Server

### Start the LLM Service
- Using uvicorn directly:
    ```bash
    cd genai
    uvicorn main:app --host 0.0.0.0 --port 8084   
- Using python3:
    ```bash
    cd genai
    python3 main.py
- Using Docker:
    ```bash
    cd genai
    docker build -t llm .
    docker run --env-file .env -p 8084:8084 llm 

## How to Use with Docker

```bash
docker compose up --build
```


---

## Tech Stack

- **Frontend**: React
- **Backend**: Spring Boot (Java)
- **GenAI Integration**: LangChain
- **AI models**: OpenAI, LLaMA
- **Database**: PostgreSQL

---

## Architecture Overview

### Top Level Architecture

### Subsystem Decomposition

---

## API documentation

---

## CI/CD Instructions

---

## Monitoring Instructions

---
