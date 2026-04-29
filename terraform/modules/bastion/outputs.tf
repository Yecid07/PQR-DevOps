output "bastion_public_ip"       { value = aws_eip.bastion.public_ip }
output "bastion_instance_id"     { value = aws_instance.bastion.id }

output "ssh_command" {
  description = "Comando SSH directo al Bastion"
  value       = "ssh -i <tu-key.pem> ec2-user@${aws_eip.bastion.public_ip}"
}

output "ssh_tunnel_command" {
  description = "Comando para abrir túnel SSH hacia RDS a través del Bastion"
  value       = "ssh -i <tu-key.pem> -L 5433:${var.rds_endpoint}:5432 ec2-user@${aws_eip.bastion.public_ip} -N"
}
