# DevOps 2025 - Team ReleaseRangers

This repository is part of the "DevOps: Engineering for Deployment and Operations" course at the Technical University of Munich, taught by Prof. Dr. Stephan Krusche and Prof. Dr. Ingo Weber.

This project is leveraging GenAI to create smart summaries of lecture materials. By automating the summarization process, it aims to provide students with a structured and up-to-date overview of their course content, enabling them to focus on understanding and applying concepts rather than summarizing.

![hero-image](/client/public/hero-releaserangers-light.png)

---

## Section Overview

- [Our Team](#our-team)
- [Student Responsibilities](#student-responsibilities)
- [Subsystem Ownership](#subsystem-ownership)
- [Key Features](#key-features)
- [Project Overview](#project-overview)
- [Quick Local Setup (Recommended)](#quick-local-setup-recommended)
- [Individual Setup Instructions](#individual-setup-instructions)
- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Database Schema](#database-schema)
- [API documentation](#api-documentation)
- [CI/CD Instructions](#ci-cd-instructions)
- [Monitoring Instructions](#monitoring-instructions)
- [Testing Instructions](#testing-instructions)
- [Code Quality: SpotBugs & Checkstyle](#code-quality-spotbugs--checkstyle)
- [Deployment Instructions](#deployment-instructions)

---

## Our Team

This project is maintained by:

- **Florian Charrot (FC)**
- **Jonathan Müller (JM)**
- **Luis Leutbecher (LL)**

---

## Student Responsibilities

- **Florian Charrot (FC)**: GenAI microservice, LLM Integration, Kubernetes Setup, Python Testing
- **Jonathan Müller (JM)**: Frontend Development, Authentication Service,
  Database Design, Client Testing, Terraform and Ansible Setup, AWS Deployment
- **Luis Leutbecher (LL)**: SpringBoot Backend, GitHub Actions, CI/CD Pipeline, Spring Boot Testing, Docker Setup, Monitoring and Observation

---

## Subsystem Ownership

- Client: Jonathan Müller (JM)
- Authentication Service: Jonathan Müller (JM)
- GenAI Service: Florian Charrot (FC)
- Course Management Service: Luis Leutbecher (LL)
- Upload Service: Luis Leutbecher (LL)

---

## Key Features

- **Upload all lecture material at a single place**
- **Get smart summaries of your lecture material categorized into learning chapters**
- **Integrate new content throughout the semester**

---

## Project Overview

Our application helps students to study efficient by leveraging LLM generated smart summaries of their lecture material. Our vision is to create one single place where one can get a summarized overview of the lecture material needed for exam preparation. We want to enable students to easily add new content throughout the semester which constantly gets summarized to always provide the student with an up-to-date overview of the current course content.

- [Problem Statement](docs/problem_statement.md): Learn about the motivation, main functionality, and user scenarios for ReleaseRangers.
- [System Architecture](docs/system_architecture.md): See the technical structure, technologies, and initial backlog for the project.

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

Attention: The JWT secret must be the same in both `.env` and
`authentication-service/src/main/resources/application.properties` files.
Currently the authentication service is using the `JWT_SECRET` from the `.env`
file, so you only need to consider this if you make manual changes to the
`authentication-service/src/main/resources/application.properties` file.

The easiest way to configure your environment for local development is to use
the provided setup script:

This script contains takes the `.env.example` file which contains all necessary
environment variables and creates the respective `.env` files for the app.
Hence, you can always adapt the `.env.example` file to your needs and run the script again
to update your `.env` files.

For giving you a head start, we have Google Gemini preconfigured as GenAI provider.
Hence for this script-setup, you need a Google Gemini API key.
You can get your own free-tier Gemini API key from [Google AI Studio](https://aistudio.google.com/app/apikey).

```bash
chmod +x setup-env.sh
```

Then run the script and follow the prompts to create and configure the necessary `.env` files:

```bash
./setup-env.sh
```

This script will automatically create and configure all required `.env` files
for both the server and genai service.

You can manually adapt the `.env` file or the `genai/.env` file to change the
GenAI provider or other settings.

E.g. for using OpenWebUI instead of Gemini, you can change the variables in the `genai/.env` file to:

```bash
LLM_API_URL=https://gpu.aet.cit.tum.de/api/chat/completions
LLM_API_KEY=<your-openwebui-api-key>
LLM_MODEL=llama3
LLM_BACKEND=openwebui
```

---

### Run locally using Docker Compose (Recommended)

To start the entire application stack (client, server, database, etc.) locally, simply run:

```bash
docker compose up --build -d
```

This will build and start all services as defined in the `docker-compose.yml` file.

---

## Individual Setup Instructions

For step-by-step instructions on setting up and running each service (client, server, GenAI/LLM service, and database) individually, see the [Start Individual Services Guide](docs/start-individual.md). This guide covers environment variable setup, dependency installation, and how to start each service separately for development or testing.

---

## Tech Stack

- **Frontend**: Next.js (React)
- **Backend**: Spring Boot (Java, JDK 21 or higher)
  - The backend services require JDK 21 or newer to build and run. You can download the JDK from [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/). Verify your installation with `java -version` to ensure you have the correct version installed.
- **GenAI Integration**: LangChain
- **Database**: PostgreSQL

---

## Architecture Overview

### Top Level Architecture

### Subsystem Decomposition

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

## API documentation

The entire API is defined using OpenAPI (see [`api-collections/openapi.yml`](api-collections/openapi.yml)).

You can view the Swagger UI via GitHub Pages:

- Open your browser and navigate to [https://aet-devops25.github.io/team-releaserangers/api/index.html](https://aet-devops25.github.io/team-releaserangers/api/index.html)

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

### Client Tests

Check the client setup from the [Start Individual Services
Guide](docs/start-individual.md) and install the necessary dependencies for the
client if not already done:

```bash
cd client
pnpm install
```

To run the playwright end-to-end (E2E) tests for the client, you have to start the whole
stack using Docker Compose. It is advised to use a fresh database to avoid conflicts with existing data.

```bash
docker compose up --build -d
```

Then still in the `client` directory, you can run the tests:

```bash
pnpm test
```

For a nice UI interface you can run the tests in headed mode:

```bash
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

## Code Quality: SpotBugs & Checkstyle & pnpm Audit

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

### How to Run pnpm Audit for the Client

To ensure the client dependencies are secure and up-to-date, you can use `pnpm audit` to check for vulnerabilities:

- Navigate to the `client` directory:

  ```sh
  cd client
  ```

- Run the audit command:

  ```sh
  pnpm audit
  ```

  This will analyze the installed dependencies and report any known
  vulnerabilities.

---

## Deployment Instructions

For deployment instructions for AWS, please refer to the [Terraform and Ansible
Setup Guide](docs/terraform_ansible_setup.md). This guide provides step-by-step
instructions on how to set up Terraform and Ansible for deploying the
application on AWS.
For kubernetes deployment, you can refer to the [Kubernetes Deployment Guide](docs/kubernetes_deployment.md).

---
