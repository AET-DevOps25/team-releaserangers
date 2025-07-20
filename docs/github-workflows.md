[← Back to Main README](../README.md)

# GitHub Workflows Documentation

## Overview

- [GitHub Workflows Documentation](#github-workflows-documentation)
  - [Overview](#overview)
  - [Build and Test Java Services](#build-and-test-java-services)
  - [Build Docker Images](#build-docker-images)
  - [Client CI](#client-ci)
  - [Lint and Test GenAI Service](#lint-and-test-genai-service)
  - [Setup the AWS environment](#setup-the-aws-environment)
  - [Build and Deploy Docker to AWS EC2](#build-and-deploy-docker-to-aws-ec2)
  - [Gitleaks Secret and KICS IaC Scan](#gitleaks-secret-and-kics-iac-scan)
  - [Deploy to Kubernetes](#deploy-to-kubernetes)

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

**Jobs:**

- **Lint and Build:**

  - **Steps:**
    1. **Checkout code:** Uses `actions/checkout@v4` to fetch the latest client code.
    2. **Setup pnpm:** Uses `pnpm/action-setup@v4` to install the latest version of pnpm, a fast and efficient package manager for Node.js projects.
    3. **Set up Node.js:** Uses `actions/setup-node@v4` to install Node.js version 22 and cache dependencies for faster builds. The cache is based on the `pnpm-lock.yaml` file to ensure consistency.
    4. **Install dependencies:** Runs `pnpm install` in the `./client` directory to install all required packages for the client application, ensuring the environment is ready for build and test.
    5. **Cache node_modules:** Uses `actions/cache@v4` to cache the `node_modules` directory for faster subsequent builds.
    6. **Security audit:** Runs `pnpm audit` in the `./client` directory to check for known vulnerabilities in dependencies, helping maintain a secure codebase.
    7. **Lint:** Runs `pnpm lint` in the `./client` directory to check code for style and quality issues, enforcing best practices and preventing common errors.
    8. **Build:** Runs `pnpm build` in the `./client` directory to compile and bundle the client application, preparing it for deployment or further testing.
    9. **Cache build output:** Uses `actions/cache@v4` to cache the `./client/dist` directory for faster access to build artifacts.
    10. **Upload build artifacts:** Uses `actions/upload-artifact@v4` to upload the build output (`./client/dist/`) as an artifact for later use.

- **End-to-End Testing:**
  - **Depends on:** Lint and Build job.
  - **Steps:**
    1. **Checkout code:** Uses `actions/checkout@v4` to fetch the latest client code.
    2. **Setup Docker Compose:** Uses `docker/setup-compose-action@v1` to prepare Docker Compose for service orchestration.
    3. **Start services:** Runs `docker compose up` to start the required services for testing.
    4. **Install Playwright Browsers:** Runs `pnpm exec playwright install --with-deps` in the `./client` directory to set up browsers for testing.
    5. **Cache Playwright browsers:** Uses `actions/cache@v4` to cache Playwright browser binaries for faster test execution.
    6. **Run Playwright tests:** Runs `pnpm exec playwright test` in the `./client` directory to execute end-to-end tests against the Dockerized services.
    7. **Upload test report:** Uses `actions/upload-artifact@v4` to upload the Playwright test report (`./client/playwright-report/`) for review.
    8. **Stop services:** Runs `docker compose down -v` to stop and clean up Docker services after testing.

---

## Lint and Test GenAI Service

**Workflow file:** `.github/workflows/lint_and_test_genai_service.yml`

**Trigger:**

- On every push to the repository.

**Jobs:**

- **Test:**
  - **Runs on:** `ubuntu-latest`
  - **Working directory:** `./genai`
  - **Steps:**
    1. **Checkout code:** Uses `actions/checkout@v4` to fetch the latest codebase for the GenAI service.
    2. **Set up Python 3.13:** Uses `actions/setup-python@v4` to install Python version 3.13 for a consistent testing environment.
    3. **Cache pip dependencies:** Uses `actions/cache@v3` to cache pip's package directory (`~/.cache/pip`) based on the hash of all `requirements*.txt` files, speeding up subsequent installs.
    4. **Install dependencies:** Installs and upgrades pip, then installs all required packages from `requirements.txt` and `requirements-test.txt` to ensure the environment is ready for linting and testing.
    5. **Lint with flake8:** Installs flake8 and runs it twice: first to catch syntax errors and undefined names (failing the build if found), and then to report all style issues as warnings, enforcing code quality and style guidelines.
    6. **Run tests:** Makes `run_tests.sh` executable and runs it to execute all tests for the GenAI service, ensuring correctness and stability.

---

## Setup the AWS environment

**Workflow file:** `.github/workflows/setup_aws_environment.yml`

**Trigger:**

- Manually via `workflow_dispatch` with required AWS credentials and optional
  Terraform state as inputs (encoded in base64).

**Jobs:**

- **Setup AWS:**
  - **Runs on:** `ubuntu-latest`
  - **Steps:**
    1. **Checkout repository:** Uses `actions/checkout@v4` to fetch the latest codebase.
    2. **Set up Terraform:** Uses `hashicorp/setup-terraform@v3` to install Terraform for infrastructure provisioning.
    3. **Set up AWS credentials:** Uses `aws-actions/configure-aws-credentials@v4` to configure AWS credentials from workflow inputs, enabling secure access to AWS resources in the `us-east-1` region.
    4. **Set up Terraform variables:** Creates a `terraform.tfvars` file in the `terraform` directory, injecting the SSH private and public keys from GitHub secrets for use by Terraform.
    5. **Restore Terraform state (if provided):** If a base64-encoded Terraform state is provided as input, decodes and restores it to `terraform/terraform.tfstate` to resume from a previous state.
    6. **Run Terraform to set up AWS environment:** Initializes and applies the Terraform configuration in the `terraform` directory, automatically approving changes to provision the AWS infrastructure.
    7. **Wait for the AWS environment to be ready:** Waits for 120 seconds to allow AWS resources to become fully available.
    8. **Output Terraform state for future use:** Outputs the base64-encoded Terraform state as a GitHub Actions notice, allowing it to be reused in future runs.

---

## Build and Deploy Docker to AWS EC2

**Workflow file:** `.github/workflows/deployAWS_docker.yml`

**Trigger:**

- Manually via `workflow_dispatch` (triggered by a user from the GitHub Actions
  UI).
- Or after a successful run of the `setup_aws_environment` workflow.

**Steps:**

1. **Checkout code:** Uses `actions/checkout@v4` to fetch the latest codebase for deployment.
2. **Copy Docker Compose file to VM:** Uses `appleboy/scp-action@v0.1.7` to securely copy the `docker-compose.aws.yml` file from the repository to the EC2 host. This file defines the services and their configuration for deployment.
3. **Create `.env.prod` on VM:** Uses `appleboy/ssh-action@v1.0.3` to SSH into the EC2 host and create a production environment file (`.env.prod`) with necessary environment variables (e.g., client and server hostnames, public API URL). This ensures services have the correct configuration for production.
4. **Deploy with Docker Compose:** Uses `appleboy/ssh-action@v1.0.3` to SSH into the EC2 host, log in to the Docker registry, and run `docker compose up` with the production environment file. The `--pull=always` flag ensures the latest images are used, and `-d` runs the containers in detached mode. Additionally, the `--remove-orphans` flag is used to clean up unused containers.

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

## Deploy to Kubernetes

**Workflow file:** `.github/workflows/deploy_kubernetes.yml`

**Trigger:**

- Manually via `workflow_dispatch` (triggered by a user from the GitHub Actions UI).
- On every merge to main.

**Jobs:**

- **Deploy:**
  - **Steps:**
    1. **Checkout code:** Uses `actions/checkout@v4` to fetch the latest codebase for Kubernetes deployment.
    2. **Set up Kubeconfig:** Creates a Kubernetes configuration file from a GitHub secret (`KUBECONFIG`), enabling secure access to the Kubernetes cluster.
    3. **Set up Helm:** Uses `azure/setup-helm@v4` to install Helm, the Kubernetes package manager used for deploying the application.
    4. **Set up kubectl:** Uses `azure/setup-kubectl@v4` to install kubectl, the Kubernetes command-line tool for managing cluster resources.
    5. **Helm dependencies:** Updates dependencies for the Helm chart by running `helm dependency update ./helm/releaserangersapp`, ensuring all required charts are available.
    6. **Generate Kubernetes Secrets:** Creates a secrets file from a template, replacing placeholders with actual secret values from GitHub secrets (`JWT_SECRET`, `LLM_API_KEY`).
    7. **Apply Kubernetes Secrets:** Deploys the generated secrets to the Kubernetes cluster using `kubectl apply`.
    8. **Deploy Helm chart:** Uses Helm to deploy or update the application by running `helm upgrade --install`. This deploys all services to the `releaserangers` namespace, creating it if it doesn't exist.
    9. **Force rollout restart:** Restarts all deployments in both the application namespace (`releaserangers`) and monitoring namespace (`ranger-observatory`) to ensure they pick up the latest changes.
    10. **Show pods in application namespace:** Displays the status of all pods in the `releaserangers` namespace for verification.
    11. **Show pods in monitoring namespace:** Displays the status of all pods in the `ranger-observatory` namespace for verification.
    12. **Get service endpoints:** Lists all services in both namespaces, showing their endpoints and access information.

---
