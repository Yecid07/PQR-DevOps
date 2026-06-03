output "bastion_ssh_command" {
  description = "Comando SSH para conectarse al Bastion"
  value       = module.bastion.ssh_command
}

output "bastion_tunnel_command" {
  description = "Comando para abrir el túnel SSH hacia RDS"
  value       = module.bastion.ssh_tunnel_command
}


output "rds_connection_via_tunnel" {
  description = "Conexión a RDS una vez abierto el túnel SSH (puerto local 5433)"
  value       = "psql -h localhost -p 5433 -U ${var.db_username} -d ${var.db_name}"
}



output "ecr_alloy_url" {
  description = "URL del repositorio ECR de Grafana Alloy"
  value       = module.ecr.alloy_repository_url
}

output "log_group_name" {
  description = "Nombre del log group en CloudWatch"
  value       = module.observability.log_group_name
}


output "eks_cluster_name" {
  value = module.eks.cluster_name
}

output "eks_cluster_endpoint" {
  value = module.eks.cluster_endpoint
}

output "kubeconfig_command" {
  description = "Comando para conectarte al cluster"
  value = "aws eks update-kubeconfig --name ${module.eks.cluster_name} --region ${var.aws_region}"
}

output "health_check_url" {
  description = "URL para verificar stable vs canary — ejecutar varias veces para ver ambas"
  value = "http://<INGRESS_ALB_DNS>/actuator/health"
}

output "ecr_app_url"   { value = module.ecr.app_repository_url }

output "rds_endpoint"  { value = module.rds.db_endpoint }

output "bastion_public_ip" { value = module.bastion.bastion_public_ip }

output "cluster_name" {
  description = "EKS cluster name"
  value       = module.eks.cluster_name
}

output "cluster_endpoint" {
  description = "EKS cluster API endpoint"
  value       = module.eks.cluster_endpoint
}

output "cluster_ca" {
  description = "EKS cluster certificate authority data"
  value       = module.eks.cluster_ca
  sensitive   = true
}

output "cluster_id" {
  description = "EKS cluster ID"
  value       = module.eks.cluster_id
}

output "alb_dns" {
  description = "DNS del ALB — úsalo en Postman"
  value       = "Ejecuta: kubectl get ingress -n pqr para obtener el DNS del ALB"
}

output "get_ingress_command" {
  description = "Comando para obtener la URL pública del ALB"
  value       = "kubectl get ingress pqr-ingress -n pqr -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'"
}

output "destroy_command" {
  value = "terraform destroy -var-file=environments/${var.environment}/terraform.tfvars -auto-approve"
}
