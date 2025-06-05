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
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-24.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical

}

resource "aws_instance" "app_server" {
  ami           = data.aws_ami.ubuntu_server_24_04.id
  instance_type = "t2.micro"

  tags = {
    Name = var.instance_name
  }
}
