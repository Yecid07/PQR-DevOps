variable "project"          { type = string }
variable "environment"      { type = string }
variable "vpc_id"           { type = string }
variable "public_subnet_id" { type = string }
variable "tags"             { type = map(string) }
variable "bastion_security_group_id" { type = string }


variable "instance_type" {
  description = "Tipo de instancia EC2 para el Bastion"
  type        = string
  default     = "t3.micro"
}

variable "public_key_path" {
  description = "Ruta local a la clave pública SSH (.pub) para el Bastion"
  type        = string
  default     = "~/.ssh/id_rsa.pub"
}

variable "allowed_ssh_cidrs" {
  description = "CIDRs autorizados para SSH al Bastion (restringe a tu IP en prod)"
  type        = list(string)
  default     = ["0.0.0.0/0"]
}

variable "rds_endpoint" {
  description = "Endpoint RDS para mostrar en el output del túnel SSH"
  type        = string
}
