[← Back to Main README](../README.md)

# AWS Deployment Guide

## Overview

This guide provides instructions for deploying the application to AWS using two approaches:

- [Automated Deployment with GitHub Actions](#deployment-steps-using-github-actions)
- [Manual Deployment Steps](#manual-deployment-steps)

## Deployment Steps using GitHub Actions

For automated deployment to AWS, we use two GitHub Actions workflows:

1. **Setup AWS Environment**: This workflow sets up the AWS environment, including creating necessary resources and configuring access.
2. **Build and Deploy to AWS EC2**: This workflow builds the Docker images and
   deploys them to an AWS EC2 instance.

### Setup AWS Environment

This workflow sets up the EC2 instance using Terraform and installs Docker and
Docker-Compose using Ansible.
To trigger this workflow, you need to manually run it from the GitHub Actions UI
and state your `aws_access_key_id`, `aws_secret_access_key`, and the
`aws_session_token` as inputs. Additionally, if you already managed the
infrastructure using Terraform, you can encode your Terraform state file
(`terraform.tfstate`) as a base64 string and provide it as an input to the
workflow. This will allow the workflow to use the existing infrastructure
without recreating it.

- For encoding the `terraform.tfstate` file, you can use the following command:
  ```bash
  cd terraform
  base64 -i terraform.tfstate
  ```

After a successful run of this workflow, the `build_and_deploy_to_aws_ec2`
workflow will be triggered automatically to copy over the
`docker-compose.aws.yml` file, the necessary directories and setup the
environment variables on the EC2 instance, and finally start the application
using Docker-Compose.

## Manual Deployment Steps

### Get Credentials

1. Install AWS CLI if it is not already installed. You can follow the
   instructions on the [AWS CLI installation
   page](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html).
2. Launch the AWS Academy lab and hit **AWS Details** to get your
   `aws_access_key_id` and `aws_secret_access_key`.
3. Copy the whole block of text and paste it into `~/.aws/credentials` file, if
   it does not exist, create it.
   The file should look like this:
   ```
   [default]
   aws_access_key_id = YOUR_ACCESS_KEY_ID
   aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
   ```
4. Download the private key file and save it in a secure location. Make sure to
   set the permissions to read/write for the owner only:
   ```bash
   chmod 400 /path/to/your/private-key.pem
   ```

### Setup Ansible

1. Install Ansible if it is not already installed. You can follow the
   instructions on the [Ansible installation
   page](https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html).

### Setup Terraform

1. Install Terraform if it is not already installed. You can follow the
   instructions on the [Terraform installation
   page](https://learn.hashicorp.com/tutorials/terraform/install-cli).
2. Copy the example variables file:
   ```bash
   cp terraform.tfvars.example terraform.tfvars
   ```
3. Edit the `terraform.tfvars` file according to your needs.
4. Navigate to the `terraform` directory:
   ```bash
   cd terraform
   ```
5. Initialize Terraform:
   ```bash
   terraform init
   ```
6. Validate the Terraform configuration:
   ```bash
   terraform validate
   ```
7. Apply the Terraform configuration:
   ```bash
   terraform apply
   ```

### Deploy to AWS

Now that the infrastructure is set up, you can deploy the application using
Docker-Compose.

- As Ansible has already installed Docker and Docker-Compose on the EC2
  instance, you can SSH into the instance:
  ```bash
  ssh -i /path/to/your/private-key.pem ubuntu@your-ec2-instance-ip-address
  ```
- Copy the `docker-compose.aws.yml` file as well as the ./prometheus, ./grafana,
  ./promtail and ./loki directories to the EC2 instance:
  ```bash
  scp -i /path/to/your/private-key.pem docker-compose.aws.yml ubuntu@your-ec2-instance-ip-address:~/
  scp -i /path/to/your/private-key.pem -r prometheus grafana promtail loki ubuntu@your-ec2-instance-ip-address:~/
  ```
- Setup the environment variables on the EC2 instance by creating a `.env.prod`
  file in the home directory:
  ```bash
   touch .env.prod
   cat > .env.prod <<EOF
   JWT_SECRET=your_jwt_secret
   CLIENT_URL=https://client.your_domain.com
   CLIENT_HOST=client.your_domain.com
   SERVER_HOST=api.your_domain.com
   GRAFANA_HOST=grafana.your_domain.com
   PROMETHEUS_HOST=prometheus.your_domain.com
   LLM_API_KEY=your_gemini_api_key
   LLM_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite-preview-06-17:generateContent
   LLM_MODEL=gemini-2.5-flash-lite-preview-06-17
   LLM_BACKEND=google
   COURSEMGMT_URL=http://coursemgmt-service:8080
   FILE_PARSING=False
   EOF
  ```
- Start the application using Docker-Compose:
  ```bash
  sudo docker compose -f docker-compose.aws.yml --env-file=.env.prod up --pull=always -d --remove-orphans
  ```
