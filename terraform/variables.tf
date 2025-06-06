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

variable "ssh_private_key" {
  description = "Private SSH key for accessing the EC2 instance"
  type        = string
  default     = ""
}
