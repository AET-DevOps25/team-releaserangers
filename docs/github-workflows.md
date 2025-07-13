# GitHub Workflows Documentation

[← Back to Main README](../README.md)

## Overview

- [Build and Test Java Services](#build-and-test-java-services)
- [Build Docker Images](#build-docker-images)
- [Client CI](#client-ci)
- [Build and Deploy Docker to AWS EC2](#build-and-deploy-docker-to-aws-ec2)
- [Gitleaks Secret and KICS IaC Scan](#gitleaks-secret-and-kics-iac-scan)

---

## Build and Test Java Services

**Workflow file:** `.github/workflows/build_and_test_java.yml`

**Trigger:**
- On every push to the repository.

**Jobs:**
- **Build:**
  - Matrix job runs for each Java service (`authentication-service`, `coursemgmt-service`, `upload-service`).
  - **Steps:**
    1. **Checkout code:** Fetches the repository code with full history for accurate builds and versioning.
    2. **Set up JDK:** Installs JDK 21 to ensure compatibility with the codebase.
    3. **Build service:** Runs `mvn clean package -DskipTests` in the service directory. This compiles and packages the code, skipping tests for faster feedback on build errors.
- **Test:**
  - Depends on the build job and runs for each service.
  - **Steps:**
    1. **Checkout code:** Ensures the latest code is available for testing.
    2. **Set up JDK:** Installs JDK 21 for test execution.
    3. **Run tests:** Executes `mvn clean package` to run all unit and integration tests, ensuring code correctness and stability.
    4. **Run SpotBugs:** Executes `mvn spotbugs:check` to perform static analysis and detect potential bugs or code quality issues.
    5. **Run Checkstyle:** Executes `mvn checkstyle:check` to enforce coding standards and style guidelines, helping maintain code quality and consistency.

---

## Build Docker Images

**Workflow file:** `.github/workflows/build_docker.yml`

**Trigger:**
- On every push to the repository (except pushes by `dependabot[bot]`).

**Steps:**
1. **Checkout code:** Uses `actions/checkout@v4` to fetch the latest codebase, ensuring Docker images are built from the most recent changes.
2. **Log in to GitHub Container Registry:** Uses `docker/login-action@v3` to authenticate with GitHub's container registry using the actor's credentials and a GitHub token. This allows pushing images securely.
3. **Set up QEMU:** Uses `docker/setup-qemu-action@v3` to enable multi-platform builds (e.g., ARM and AMD64), ensuring images are compatible with various deployment targets.
4. **Set up Docker Buildx:** Uses `docker/setup-buildx-action@v3` to enable advanced Docker build features, such as multi-platform builds and caching.
5. **Build and push Docker images:** Uses `docker/build-push-action@v5` to build and push images for each service (nginx-gateway, postgresdb, authentication-service, coursemgmt-service, upload-service, genai-service, client). Each image is built for both `linux/amd64` and `linux/arm64` platforms and tagged as `latest` in the GitHub Container Registry. This step ensures all services are containerized and ready for deployment.

---

## Client CI

**Workflow file:** `.github/workflows/client_ci.yml`

**Trigger:**
- On every push to any branch.

**Steps:**
1. **Checkout code:** Uses `actions/checkout@v4` to fetch the latest client code.
2. **Setup pnpm:** Uses `pnpm/action-setup@v4` to install the latest version of pnpm, a fast and efficient package manager for Node.js projects.
3. **Set up Node.js:** Uses `actions/setup-node@v4` to install Node.js version 22 and cache dependencies for faster builds. The cache is based on the `pnpm-lock.yaml` file to ensure consistency.
4. **Install dependencies:** Runs `pnpm install` to install all required packages for the client application, ensuring the environment is ready for build and test.
5. **Security audit:** Runs `pnpm audit` to check for known vulnerabilities in dependencies, helping maintain a secure codebase.
6. **Lint:** Runs `pnpm lint` to check code for style and quality issues, enforcing best practices and preventing common errors.
7. **Build:** Runs `pnpm build` to compile and bundle the client application, preparing it for deployment or further testing.

---

## Build and Deploy Docker to AWS EC2

**Workflow file:** `.github/workflows/deployAWS_docker.yml`

**Trigger:**
- Manually via `workflow_dispatch` (triggered by a user from the GitHub Actions UI).

**Steps:**
1. **Checkout code:** Uses `actions/checkout@v4` to fetch the latest codebase for deployment.
2. **Copy Docker Compose file to VM:** Uses `appleboy/scp-action@v0.1.7` to securely copy the `docker-compose.aws.yml` file from the repository to the EC2 host. This file defines the services and their configuration for deployment.
3. **Create `.env.prod` on VM:** Uses `appleboy/ssh-action@v1.0.3` to SSH into the EC2 host and create a production environment file (`.env.prod`) with necessary environment variables (e.g., client and server hostnames, public API URL). This ensures services have the correct configuration for production.
4. **Deploy with Docker Compose:** Uses `appleboy/ssh-action@v1.0.3` to SSH into the EC2 host, log in to the Docker registry, and run `docker compose up` with the production environment file. The `--pull=always` flag ensures the latest images are used, and `-d` runs the containers in detached mode. This step orchestrates the deployment of all services on the EC2 instance.

---

## Gitleaks Secret and KICS IaC Scan

**Workflow file:** `.github/workflows/securityWorkflow.yml`

**Trigger:**
- On every push to the repository.

**Jobs:**
- **Gitleaks:**
  - **Steps:**
    1. **Checkout code:** Fetches the latest codebase for scanning.
    2. **Run Gitleaks:** Uses `gitleaks/gitleaks-action@v2` to scan the repository for secrets (e.g., API keys, credentials) using a custom configuration file (`gitleaks.toml`). This helps prevent accidental exposure of sensitive information.
- **KICS IaC Scan:**
  - **Steps:**
    1. **Checkout code:** Ensures the latest code is available for scanning.
    2. **Run KICS Scan:** Uses `checkmarx/kics-github-action@v2.1.11` to scan Infrastructure-as-Code files (e.g., Terraform, Docker Compose) for security vulnerabilities and misconfigurations. Results are saved to `./kicsResults/`.
    3. **Display KICS results:** Outputs the scan results to the workflow log for review and remediation.

---

> This documentation provides a comprehensive summary of all CI/CD workflows, their triggers, and detailed explanations of each step performed for every pipeline in this repository.
