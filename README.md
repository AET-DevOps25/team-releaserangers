# DevOps 2025 - Team ReleaseRangers

This is a university project designed to enhance students learning experience by leveraging GenAI to create summaries of lecture material throughout the semester. We want to help students optimize their study time by automating the summarization process, allowing them to focus on understanding and applying rather than summarizing.

---

# Section Overview

- [Our Team](#our-team)
- [Student Responsibilities](#student-responsibilities)
- [Subsystem Ownership](#subsystem-ownership)
- [Key Features](#key-features)
- [Project Overview](#project-overview)
- [Setup Instructions](#setup-instructions)
  - [Client Setup](#client-setup)
  - [Server Setup](#server-setup)
  - [LLM Service Setup](#llm-service-setup)
- [Running the Application](#running-the-application)
  - [Start the Database](#start-the-database)
  - [Start the Client](#start-the-client)
  - [Start the Server](#start-the-server)
  - [Start the LLM Service](#start-the-llm-service)
- [How to Use with Docker](#how-to-use-with-docker)
- [Tech Stack](#tech-stack)
- [Database Schema](#database-schema)
- [Architecture Overview](#architecture-overview)
- [API documentation](#api-documentation)
- [CI/CD Instructions](#cicd-instructions)
- [Monitoring Instructions](#monitoring-instructions)
- [Code Quality: SpotBugs & Checkstyle](#code-quality-spotbugs--checkstyle)

---

## Our Team

This project is maintained by:

- **Florian Charrot (FC)**
- **Jonathan Müller (JM)**
- **Luis Leutbecher (LL)**

---

## Student Responsibilities
- **Florian Charrot (FC)**: GenAI microservice, LLM Integration, Kubernetes Setup, Python Testing
- **Jonathan Müller (JM)**: Frontend Development, Database Design, Terraform and Ansible Setup, Client Testing, Authentication Service
- **Luis Leutbecher (LL)**: SpringBoot Backend, GitHub Actions, CI/CD Pipeline, Spring Boot Testing, Docker Setup

## Subsystem Ownership

- Client: Jonathan Müller (JM)
- Authentication Service: Jonathan Müller (JM)
- GenAI Service: Florian Charrot (FC)
- Course Management Service: Luis Leutbecher (LL)
- Upload Service: Luis Leutbecher (LL)

---

## Key Features

- **Upload all lecture material at a single place**
- **Structure relevant information into learning chapters**
- **Keep uploading whenever you receive new material**

---

## Project Overview

Our application helps students to study efficient by leveraging LLM generated smart summaries of their lecture material. Our vision is to create one single place where one can get a summarized overview of the lecture material needed for exam preparation. We want to enable students to easily add new content throughout the semester which constantly gets summarized to always provide the student with an up-to-date overview of the current course content.

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

### Start the Client

### Start the Server

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

## Database Schema

Our application uses PostgreSQL as the primary database with tables distributed across three microservices:

![Database Schema](docs/database_schema.png)

### Schema Documentation

- **DBML File**: [database_schema.dbml](docs/database_schema.dbml) - Import this into [dbdiagram.io](https://dbdiagram.io) for interactive editing

### Key Tables

- **users** (Authentication Service): User accounts and authentication
- **courses** (Course Management): Course information and metadata
- **chapters** (Course Management): Individual learning chapters within courses
- **uploaded_files** (Upload Service): File uploads associated with courses

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

## Code Quality: SpotBugs & Checkstyle

### How to Run SpotBugs and Checkstyle

SpotBugs and Checkstyle are integrated into the Maven build lifecycle for the server and each microservice (authentication-service, coursemgmt-service, upload-service).

You can run these tools manually or as part of the Maven build:

- **To run both SpotBugs and Checkstyle for all modules:**
  ```sh
  cd server
  mvn verify
  ```
  This will execute both plugins as part of the `verify` phase.

- **To run only SpotBugs:**
  ```sh
  mvn spotbugs:check
  ```

- **To run only Checkstyle:**
  ```sh
  mvn checkstyle:check
  ```

- **To run for a specific microservice:**
  ```sh
  cd server/<microservice-folder>
  mvn verify
  ```
  Replace `<microservice-folder>` with `authentication-service`, `coursemgmt-service`, or `upload-service`.

### Maven Phase Integration
- **Checkstyle** runs during the `validate` and `verify` phases.
- **SpotBugs** runs during the `verify` phase.

If you run `mvn verify`, both tools will be executed and any violations will fail the build.

---
