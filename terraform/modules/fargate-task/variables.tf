variable "project" { type = string }
variable "environment" { type = string }
variable "aws_region" { type = string }
variable "cluster_id" { type = string }
variable "cluster_name" { type = string }
variable "app_image" { type = string }
variable "alloy_image" { type = string }
variable "app_port" { type = number }
variable "task_cpu" { type = string }
variable "task_memory" { type = string }
variable "desired_count" { type = number }
variable "execution_role_arn" { type = string }
variable "task_role_arn" { type = string }
variable "private_subnet_ids" { type = list(string) }
variable "ecs_security_group_id" { type = string }
variable "target_group_arn" { type = string }
variable "db_url" { type = string }
variable "db_username" { type = string }
variable "db_password" {
  type      = string
  sensitive = true
}
variable "book_order_url" { type = string }
variable "book_order_threshold" { type = string }
variable "grafana_api_key" {
  type      = string
  sensitive = true
}
variable "log_group_name" { type = string }
variable "tags" { type = map(string) }
