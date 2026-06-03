module "network" {
  source             = "../../modules/network"
  project            = "pqr"
  environment        = "dev"
  vpc_cidr           = var.vpc_cidr
  public_subnets     = var.public_subnets
  private_subnets    = var.private_subnets
  availability_zones = var.availability_zones
}

module "iam" {
  source      = "../../modules/iam"
  project     = "pqr"
  environment = "dev"
}

module "ecr" {
  source      = "../../modules/ecr"
  project     = "pqr"
  environment = "dev"
}

module "rds" {
  source             = "../../modules/rds"
  project            = "pqr"
  environment        = "dev"
  vpc_id             = module.network.vpc_id
  private_subnets    = module.network.private_subnet_ids
  db_name            = var.db_name
  db_username        = var.db_username
  db_password        = var.db_password
  db_instance_class  = var.db_instance_class
}

module "bastion" {
  source                = "../../modules/bastion"
  project               = "pqr"
  environment           = "dev"
  vpc_id                = module.network.vpc_id
  public_subnet_id      = module.network.public_subnet_ids[0]
  bastion_public_key_path = var.bastion_public_key_path
  instance_type         = var.bastion_instance_type
  rds_endpoint          = module.rds.db_endpoint
}

module "observability" {
  source      = "../../modules/observability"
  project     = "pqr"
  environment = "dev"
}

# ── EKS CLUSTER ───────────────────────────────────────────
module "eks" {
  source               = "../../modules/eks"
  project              = "pqr"
  environment          = "dev"
  public_subnets       = module.network.public_subnet_ids
  private_subnets      = module.network.private_subnet_ids
  eks_cluster_role_arn = module.iam.eks_cluster_role_arn
  eks_node_role_arn    = module.iam.eks_node_role_arn
}

# ── DEPLOYMENTS + SERVICE + INGRESS ───────────────────────
module "k8s_canary" {
  source      = "../../modules/k8s-canary"
  project     = "pqr"

  stable_image          = var.app_image
  canary_image          = var.canary_image
  app_port              = 8080
  stable_replicas       = 4
  canary_replicas       = 1
  stable_traffic_weight = var.stable_traffic_weight
  canary_traffic_weight = var.canary_traffic_weight

  db_url               = "jdbc:postgresql://${module.rds.db_endpoint}/${var.db_name}"
  db_username          = var.db_username
  db_password          = var.db_password
  grafana_api_key      = var.grafana_api_key
  book_order_url       = var.book_order_url
  book_order_threshold = var.book_order_threshold

  depends_on = [module.eks, module.alb_controller]
}

module "alb_controller" {
  source = "../../modules/alb-controller"

  project           = "pqr"
  environment       = "dev"
  cluster_name      = module.eks.cluster_name
  oidc_provider_arn = module.eks.oidc_provider_arn
  oidc_provider_url = module.eks.oidc_provider
  vpc_id            = module.network.vpc_id
  aws_region        = var.aws_region

  depends_on = [module.eks]
}