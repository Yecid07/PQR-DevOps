locals {
  common_tags = {
    Project     = var.project
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

data "aws_caller_identity" "current" {}

module "network" {
  source             = "./modules/network"

  project            = var.project
  environment        = var.environment
  vpc_cidr           = var.vpc_cidr
  public_subnets     = var.public_subnets
  private_subnets    = var.private_subnets
  availability_zones = var.availability_zones

  tags = local.common_tags
}

module "iam" {
  source      = "./modules/iam"
  project     = var.project
  environment = var.environment
  aws_region  = var.aws_region
  account_id  = data.aws_caller_identity.current.account_id
  tags        = local.common_tags
}

module "eks" {
  source = "./modules/eks"

  project              = var.project
  environment          = var.environment
  public_subnets       = module.network.public_subnet_ids
  private_subnets      = module.network.private_subnet_ids
  eks_cluster_role_arn = module.iam.eks_cluster_role_arn
  eks_node_role_arn    = module.iam.eks_node_role_arn
  tags                 = local.common_tags

  depends_on = [module.iam, module.network]
}

module "ecr" {
  source      = "./modules/ecr"
  project     = var.project
  environment = var.environment
  tags        = local.common_tags
}

module "rds" {
  source                = "./modules/rds"
  project               = var.project
  environment           = var.environment
  vpc_id                = module.network.vpc_id
  private_subnet_ids    = module.network.private_subnet_ids
  rds_security_group_id = module.network.rds_security_group_id
  db_name               = var.db_name
  db_username           = var.db_username
  db_password           = var.db_password
  db_instance_class     = var.db_instance_class
  tags                  = local.common_tags
}

module "bastion" {
  source                    = "./modules/bastion"
  project                   = var.project
  environment               = var.environment
  vpc_id                    = module.network.vpc_id
  public_subnet_id          = module.network.public_subnet_id
  public_key_path           = var.bastion_public_key_path
  instance_type             = var.bastion_instance_type
  rds_endpoint              = module.rds.db_endpoint
  bastion_security_group_id = module.network.bastion_security_group_id
  tags                      = local.common_tags
}

module "observability" {
  source      = "./modules/observability"
  project     = var.project
  environment = var.environment
  aws_region  = var.aws_region
  tags        = local.common_tags
}

module "k8s_canary" {
  source = "./modules/k8s-canary"

  project     = var.project
  namespace    = "pqr"

  stable_image = var.app_image
  canary_image = var.canary_image

  app_port        = 8080
  stable_replicas = 1
  canary_replicas = 1

  stable_traffic_weight = var.stable_traffic_weight
  canary_traffic_weight = var.canary_traffic_weight

  db_url      = "jdbc:postgresql://${module.rds.db_endpoint}/${var.db_name}"
  db_username = var.db_username
  db_password = var.db_password

  grafana_api_key      = var.grafana_api_key
  book_order_url       = var.book_order_url
  book_order_threshold = var.book_order_threshold

  depends_on = [
    module.eks
  ]
}