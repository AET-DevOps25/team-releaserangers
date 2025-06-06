output "instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.app_server.id
}

output "instance_ami" {
  description = "AMI ID used for the EC2 instance"
  value       = data.aws_ami.ubuntu_server_24_04.id
}

output "instance_public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.app_server.public_ip
}
