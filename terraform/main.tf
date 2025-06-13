terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.99.1"
    }
  }

  required_version = ">= 1.12.1"
}

provider "aws" {
  region = "us-east-1"
}

data "aws_ami" "ubuntu_server_24_04" {
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical

}

resource "aws_security_group" "app_server_sg" {
  name        = "${var.env_prefix}-app-server-sg"
  description = "Security group for the application server"

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.env_prefix}-app-server-sg"
  }
}
resource "aws_instance" "app_server" {
  ami                         = data.aws_ami.ubuntu_server_24_04.id
  instance_type               = "t2.micro"
  key_name                    = "vockey"
  associate_public_ip_address = true
  vpc_security_group_ids      = [aws_security_group.app_server_sg.id]

  tags = {
    Name = "${var.env_prefix}-${var.instance_name}"
  }
}

resource "null_resource" "configure_instance" {
  depends_on = [aws_instance.app_server]

  provisioner "local-exec" {
    command = <<-EOT
      # Wait for SSH to become available
      while ! nc -z ${aws_instance.app_server.public_ip} 22; do
        echo "Waiting for SSH connection..."
        sleep 10
      done
      
      # Create inventory file
      cat > ../ansible/inventory.ini <<EOF
      [app_server]
      ${aws_instance.app_server.public_ip}
      EOF
      
      # Run Ansible playbook with host key checking disabled
      ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook \
        -i ../ansible/inventory.ini \
        -u ubuntu \
        --private-key=${var.ssh_private_key} \
        --extra-vars 'ansible_python_interpreter=/usr/bin/python3' \
        ../ansible/playbook.yml
    EOT
  }
}
