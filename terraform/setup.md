# Setup Terraform

## Get Credentials

1. Launch the AWS Academy lab and hit **AWS Details** to get your
   `aws_access_key_id` and `aws_secret_access_key`.
2. Copy the whole block of text and paste it into `~/.aws/credentials` file, if
   it does not exist, create it.
   The file should look like this:
   ```
   [default]
   aws_access_key_id = YOUR_ACCESS_KEY_ID
   aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
   ```
3. Navigate to the `terraform` directory:
   ```bash
   cd terraform
   ```
4. Initialize Terraform:
   ```bash
   terraform init
   ```
5. Validate the Terraform configuration:
   ```bash
   terraform validate
   ```
6. Apply the Terraform configuration:
   ```bash
   terraform apply
   ```
