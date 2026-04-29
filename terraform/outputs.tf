output "alb_dns_name" {
  description = "DNS público del Application Load Balancer"
  value       = module.load_balancer.alb_dns_name
}

output "app_url" {
  description = "URL de acceso a la aplicación PQR"
  value       = "http://${module.load_balancer.alb_dns_name}/api/v2"
}

# ── Bastion ──────────────────────────────────────────────────────────────────
output "bastion_public_ip" {
  description = "IP pública del Bastion Host"
  value       = module.bastion.bastion_public_ip
}

output "bastion_ssh_command" {
  description = "Comando SSH para conectarse al Bastion"
  value       = module.bastion.ssh_command
}

output "bastion_tunnel_command" {
  description = "Comando para abrir el túnel SSH hacia RDS"
  value       = module.bastion.ssh_tunnel_command
}

# ── RDS ──────────────────────────────────────────────────────────────────────
output "rds_endpoint" {
  description = "Endpoint privado de la base de datos RDS"
  value       = module.rds.db_endpoint
}

output "rds_connection_via_tunnel" {
  description = "Conexión a RDS una vez abierto el túnel SSH (puerto local 5433)"
  value       = "psql -h localhost -p 5433 -U ${var.db_username} -d ${var.db_name}"
}

# ── ECS ──────────────────────────────────────────────────────────────────────
output "ecs_cluster_name" {
  description = "Nombre del cluster ECS"
  value       = module.ecs.cluster_name
}

# ── ECR ──────────────────────────────────────────────────────────────────────
output "ecr_app_url" {
  description = "URL del repositorio ECR de la aplicación"
  value       = module.ecr.app_repository_url
}

output "ecr_alloy_url" {
  description = "URL del repositorio ECR de Grafana Alloy"
  value       = module.ecr.alloy_repository_url
}

# ── Observabilidad ────────────────────────────────────────────────────────────
output "log_group_name" {
  description = "Nombre del log group en CloudWatch"
  value       = module.observability.log_group_name
}

# ── Destrucción ───────────────────────────────────────────────────────────────
output "destroy_command" {
  description = "Comando para destruir toda la infraestructura al finalizar"
  value       = "terraform destroy -var-file=environments/${var.environment}/terraform.tfvars -auto-approve"
}
