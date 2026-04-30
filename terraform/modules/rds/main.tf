resource "aws_db_subnet_group" "main" {
  name       = "${var.project}-${var.environment}-db-subnet-group"
  subnet_ids = var.private_subnet_ids
  tags = merge(var.tags, { Name = "${var.project}-${var.environment}-db-subnet-group" })
}

resource "aws_db_parameter_group" "postgres" {
  name   = "${var.project}-${var.environment}-pg15"
  family = "postgres15"

  parameter {
    name  = "log_connections"
    value = "1"
  }
  parameter {
    name  = "log_disconnections"
    value = "1"
  }

  tags = var.tags
}

resource "aws_db_instance" "main" {
  identifier     = "${var.project}-${var.environment}"
  engine         = "postgres"
  engine_version = "15.12"
  instance_class = var.db_instance_class

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  # Almacenamiento
  allocated_storage     = 20
  max_allocated_storage = 100
  storage_type          = "gp2"
  storage_encrypted = true

  # Red — privately_accessible (acceso solo por túnel SSH via Bastion)
  db_subnet_group_name = aws_db_subnet_group.main.name
  vpc_security_group_ids = [var.rds_security_group_id]
  publicly_accessible  = false
  multi_az             = false

  parameter_group_name = aws_db_parameter_group.postgres.name

  # Backups
  backup_retention_period = 0

  # Protección — false para poder hacer destroy
  deletion_protection       = false
  skip_final_snapshot       = true
  final_snapshot_identifier = null

  tags = merge(var.tags, { Name = "${var.project}-${var.environment}-rds" })
}
