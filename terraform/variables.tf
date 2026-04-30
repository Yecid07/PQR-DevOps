variable "aws_region" {
  description = "Región AWS donde se despliega la infraestructura"
  type        = string
  default     = "us-east-2"
}

variable "environment" {
  description = "Entorno de despliegue: dev | staging | prod"
  type        = string
}

variable "project" {
  description = "Nombre del proyecto"
  type        = string
  default     = "pqr"
}

# Red
variable "vpc_cidr" {
  description = "CIDR block de la VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnets" {
  description = "CIDRs de subnets públicas"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnets" {
  description = "CIDRs de subnets privadas"
  type        = list(string)
  default     = ["10.0.11.0/24", "10.0.12.0/24"]
}

variable "availability_zones" {
  description = "AZs disponibles"
  type        = list(string)
  default     = ["us-east-2a", "us-east-2b"]
}

variable "db_name" {
  description = "Nombre de la base de datos"
  type        = string
  default     = "postgres"
}

variable "db_username" {
  description = "Usuario de la base de datos"
  type        = string
  default     = "postgres"
}

variable "db_password" {
  description = "Contraseña de la base de datos"
  type        = string
  sensitive   = true
}

variable "db_instance_class" {
  description = "Tipo de instancia RDS"
  type        = string
  default     = "db.t3.micro"
}

variable "app_image" {
  description = "URI de la imagen Docker de la aplicación"
  type        = string
}

variable "alloy_image" {
  description = "URI de la imagen Docker de Grafana Alloy"
  type        = string
}

variable "app_port" {
  description = "Puerto de la aplicación Spring Boot"
  type        = number
  default     = 8080
}

variable "task_cpu" {
  description = "CPU de la task Fargate (unidades)"
  type        = string
  default     = "512"
}

variable "task_memory" {
  description = "Memoria de la task Fargate (MB)"
  type        = string
  default     = "1024"
}

variable "desired_count" {
  description = "Número de tareas Fargate deseadas"
  type        = number
  default     = 1
}

variable "grafana_api_key" {
  description = "API Key de Grafana Cloud para Alloy"
  type        = string
  sensitive   = true
}

variable "book_order_url" {
  description = "URL del servicio externo de compra de libros"
  type        = string
  default     = "http://18.224.71.133:8080/api/v2/books/purchase"
}

variable "book_order_threshold" {
  description = "Umbral de pedido de libros"
  type        = string
  default     = "5"
}

variable "bastion_public_key_path" {
  description = "Ruta a la clave pública SSH para el Bastion Host"
  type        = string
  default     = "~/.ssh/id_rsa.pub"
}

variable "bastion_instance_type" {
  description = "Tipo de instancia EC2 del Bastion"
  type        = string
  default     = "t3.micro"
}
