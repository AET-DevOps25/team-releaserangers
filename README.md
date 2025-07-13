# DevOps 2025 - Team ReleaseRangers

This is a university project designed to enhance students learning experience by leveraging GenAI to create summaries of lecture material throughout the semester. We want to help students optimize their study time by automating the summarization process, allowing them to focus on understanding and applying rather than summarizing.

---

# Section Overview

- [Our Team](#our-team)
- [Student Responsibilities](#student-responsibilities)
- [Subsystem Ownership](#subsystem-ownership)
- [Key Features](#key-features)
- [Project Overview](#project-overview)
- [Quick Local Setup (Recommended)](#quick-local-setup-recommended)
- [Individual Setup Instructions](#individual-setup-instructions)
- [Tech Stack](#tech-stack)
- [Database Schema](#database-schema)
- [Architecture Overview](#architecture-overview)
- [API documentation](#api-documentation)
- [CI/CD Instructions](#ci-cd-instructions)
- [Monitoring Instructions](#monitoring-instructions)
- [Testing Instructions](#testing-instructions)
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
- **Jonathan Müller (JM)**: Frontend Development, Database Design, Terraform and Ansible Setup, Client Testing, Authentication Service, AWS Deployment
- **Luis Leutbecher (LL)**: SpringBoot Backend, GitHub Actions, CI/CD Pipeline, Spring Boot Testing, Docker Setup, Monitoring and Observation

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

## Quick Local Setup (Recommended)

### Clone the Repository

To get started, clone the repository:

```bash
git clone https://github.com/AET-DevOps25/team-releaserangers.git
```

And navigate into the project directory:

```bash
cd team-releaserangers
```

### Environment Configuration

> **⚠️ Note:** Make sure to configure the `JWT_SECRET` and `LLM_API_KEY` environment variables before running the project locally. These are required for authentication and GenAI features to work.

Attention: The JWT secret must be the same in both `.env` and `authentication-service/src/main/resources/application.properties` files.

The easiest way to configure your environment for local development is to use the provided setup script:

You can get your own free-tier gemini API key from [Google AI Studio](https://aistudio.google.com/app/apikey).

```bash
chmod +x setup-env.sh
```

Then run the script and follow the prompts to create and configure the necessary `.env` files:

```bash
./setup-env.sh
```

This script will automatically create and configure all required `.env` files for both the server, genai and client.
If you set up the `.env` files yourself please ensure that secrets like `JWT_SECRET` are synchronized.

---

### Run locally using Docker Compose (Recommended)

To start the entire application stack (client, server, database, etc.) locally, simply run:

```bash
docker compose up --build
```

This will build and start all services as defined in the `docker-compose.yml` file.

---

## Individual Setup Instructions

For step-by-step instructions on setting up and running each service (client, server, GenAI/LLM service, and database) individually, see the [Start Individual Services Guide](docs/start-individual.md). This guide covers environment variable setup, dependency installation, and how to start each service separately for development or testing.

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

The entire API is defined using OpenAPI (see [`api-collections/openapi.yml`](api-collections/openapi.yml)).

You can view the Swagger UI via GitHub Pages (if enabled for this repository):

- Open your browser and navigate to `https://aet-devops25.github.io/team-releaserangers/api/index.html`

This provides a complete, interactive overview of all endpoints, request/response formats, and authentication details.

---

## CI/CD Instructions

### Documentation

For detailed documentation on the CI/CD workflows, please refer to the following documentation:
- [GitHub Workflows Documentation](docs/github-workflows.md)
- [Start Individual Services Guide](docs/start-individual.md)

---

## Monitoring Instructions

---

## Testing Instructions

### How to Run Tests

### Client Tests

Check the client setup from above and install the necessary dependencies for the client:

```bash
cd client
pnpm install
```

To run the playwright e2e tests for the client, you have to start the whole
stack using Docker Compose. It is advised to use a fresh database to avoid conflicts with existing data.

```bash
docker compose up --build -d
```

Then you can run the tests using Playwright:

```bash
cd client
pnpm test
```

For a nice UI interface you can run the tests in headed mode:

```bash
cd client
pnpm test:ui
```

### Server Tests

To run tests for the server and each microservice, you can use Maven commands. Each microservice has its own set of tests, and you can run them individually or for the entire server.
For the entire server, navigate to the `server` directory and run:

```bash
cd server
mvn clean package
```

For individual microservices, navigate to the specific service directory and run:

```bash
cd server/authentication-service
mvn clean package

cd ../coursemgmt-service
mvn clean package

cd ../upload-service
mvn clean package
```

### GenAI Service Tests

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
