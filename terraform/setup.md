# Setup Terraform

## Get Credentials

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

## Setup Ansible

1. Install Ansible if it is not already installed. You can follow the
   instructions on the [Ansible installation
   page](https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html).

## Setup Terraform

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

## PostgreSQL Persistent Storage

The Terraform configuration includes an EBS volume for PostgreSQL data persistence. After running `terraform apply`, you'll need to set up the volume for use with PostgreSQL.

See the [PostgreSQL Persistent Storage documentation](../docs/postgresql-persistent-storage.md) for detailed instructions on:
- Formatting and mounting the EBS volume
- Configuring PostgreSQL to use persistent storage
- Setting up automated mounting with Ansible

The EBS volume configuration can be customized in `terraform.tfvars`:
- `ebs_volume_size`: Size in GB (default: 20)
- `ebs_volume_type`: Volume type (default: gp3)
- `ebs_volume_encrypted`: Encryption enabled (default: true)
