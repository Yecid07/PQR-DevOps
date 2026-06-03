variable "project"              { type = string }
variable "environment"          { type = string }
variable "public_subnets"       { type = list(string) }
variable "private_subnets"      { type = list(string) }
variable "eks_cluster_role_arn" { type = string }
variable "eks_node_role_arn"    { type = string }
variable "tags"                 { type = map(string) }
