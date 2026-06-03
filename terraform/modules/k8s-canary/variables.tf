variable "project" {
  type = string
}

variable "namespace" {
  type    = string
  default = "pqr"
}

variable "stable_image" {
  type = string
}

variable "canary_image" {
  type = string
}

variable "app_port" {
  type    = number
  default = 8080
}

variable "stable_replicas" {
  type    = number
  default = 4
}

variable "canary_replicas" {
  type    = number
  default = 1
}

variable "stable_traffic_weight" {
  type    = number
  default = 80
}

variable "canary_traffic_weight" {
  type    = number
  default = 20
}

variable "db_url" {
  type      = string
  sensitive = true
}

variable "db_username" {
  type      = string
  sensitive = true
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "grafana_api_key" {
  type      = string
  sensitive = true
}

variable "book_order_url" {
  type = string
}

variable "book_order_threshold" {
  type = string
}