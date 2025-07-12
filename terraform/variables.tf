variable "env_prefix" {
  description = "Prefix for the environment, used in resource names"
  type        = string
  default     = "dev"
}

variable "instance_name" {
  description = "Value of the Name tag for the EC2 instance"
  type        = string
  default     = "ReleaseRangersAppServer"
}
variable "instance_type" {
  description = "Type of the EC2 instance"
  type        = string
  default     = "t2.micro"
}

variable "ssh_public_key" {
  description = "Public SSH key for accessing the EC2 instance"
  type        = string
  sensitive   = true
}

variable "ssh_private_key" {
  description = "Path to private SSH key file for accessing the EC2 instance"
  type        = string
  sensitive   = true
}

variable "block_device_size" {
  description = "Size of the root block device in GB"
  type        = number
  default     = 48
}