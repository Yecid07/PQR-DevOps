locals {
  common_tags = {
    Project     = var.project
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}


module "ecr" {
  source      = "./modules/ecr"
  project     = var.project
  environment = var.environment
  tags        = local.common_tags
}

module "network" {
  source             = "./modules/network"
  project            = var.project
  environment        = var.environment
  vpc_cidr           = var.vpc_cidr
  public_subnets     = var.public_subnets
  private_subnets    = var.private_subnets
  availability_zones = var.availability_zones
  tags               = local.common_tags
}

module "iam" {
  source      = "./modules/iam"
  project     = var.project
  environment = var.environment
  aws_region  = var.aws_region
  account_id  = data.aws_caller_identity.current.account_id
  tags        = local.common_tags
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

module "load_balancer" {
  source                = "./modules/load-balancer"
  project               = var.project
  environment           = var.environment
  vpc_id                = module.network.vpc_id
  public_subnet_ids     = module.network.public_subnet_ids
  alb_security_group_id = module.network.alb_security_group_id
  app_port              = var.app_port
  tags                  = local.common_tags
}

module "ecs" {
  source      = "./modules/ecs"
  project     = var.project
  environment = var.environment
  tags        = local.common_tags
}

module "observability" {
  source      = "./modules/observability"
  project     = var.project
  environment = var.environment
  aws_region  = var.aws_region
  tags        = local.common_tags
}

module "fargate_task" {
  source      = "./modules/fargate-task"
  project     = var.project
  environment = var.environment
  aws_region  = var.aws_region

  cluster_id         = module.ecs.cluster_id
  cluster_name       = module.ecs.cluster_name
  app_image          = var.app_image
  alloy_image        = var.alloy_image
  app_port           = var.app_port
  task_cpu           = var.task_cpu
  task_memory        = var.task_memory
  desired_count      = var.desired_count
  execution_role_arn = module.iam.ecs_execution_role_arn
  task_role_arn      = module.iam.ecs_task_role_arn

  private_subnet_ids    = module.network.private_subnet_ids
  ecs_security_group_id = module.network.ecs_security_group_id
  target_group_arn      = module.load_balancer.target_group_arn

  db_url               = "jdbc:postgresql://${module.rds.db_endpoint}/postgres"
  db_username          = var.db_username
  db_password          = var.db_password
  book_order_url       = var.book_order_url
  book_order_threshold = var.book_order_threshold

  grafana_api_key = var.grafana_api_key
  log_group_name  = module.observability.log_group_name

  tags = local.common_tags
}

data "aws_caller_identity" "current" {}
