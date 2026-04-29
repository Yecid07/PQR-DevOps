module "pqr_dev" {
  source = "../../"

  environment          = "dev"
  aws_region           = "us-east-2"
  project              = "pqr"
  vpc_cidr             = "10.0.0.0/16"
  public_subnets       = ["10.0.1.0/24", "10.0.2.0/24"]
  private_subnets      = ["10.0.11.0/24", "10.0.12.0/24"]
  availability_zones   = ["us-east-2a", "us-east-2b"]
  db_name              = var.db_name
  db_username          = var.db_username
  db_password          = var.db_password
  db_instance_class    = "db.t3.micro"
  app_image            = var.app_image
  alloy_image          = var.alloy_image
  app_port             = 8080
  task_cpu             = "512"
  task_memory          = "1024"
  desired_count        = 1
  grafana_api_key      = var.grafana_api_key
  book_order_url       = var.book_order_url
  book_order_threshold = var.book_order_threshold
}
