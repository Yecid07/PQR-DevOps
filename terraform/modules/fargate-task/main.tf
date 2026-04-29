# ── Task Definition (replica exacta de la task proporcionada) ────────────────
resource "aws_ecs_task_definition" "pqr" {
  family             = "${var.project}-task"
  requires_compatibilities = ["FARGATE"]
  network_mode       = "awsvpc"
  cpu                = var.task_cpu
  memory             = var.task_memory
  execution_role_arn = var.execution_role_arn
  task_role_arn      = var.task_role_arn

  container_definitions = jsonencode([
    {
      name      = "pqr-management"
      image     = var.app_image
      cpu       = 0
      essential = true

      portMappings = [
        {
          containerPort = var.app_port
          hostPort      = var.app_port
          protocol      = "tcp"
        }
      ]

      environment = [
        { name = "SPRING_DATASOURCE_USERNAME", value = var.db_username },
        { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
        { name = "SPRING_DATASOURCE_URL", value = var.db_url },
        { name = "PQR_BOOK_ORDER_THRESHOLD", value = var.book_order_threshold },
        { name = "SERVICES_BOOK_ORDER_URL", value = var.book_order_url }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = var.log_group_name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = var.project
        }
      }

      mountPoints = []
      volumesFrom = []
      systemControls = []
    },
    {
      name      = "grafana-alloy"
      image     = var.alloy_image
      cpu       = 0
      essential = false

      command = [
        "run",
        "--server.http.listen-addr=0.0.0.0:12345",
        "/etc/alloy/config.alloy"
      ]

      environment = [
        { name = "GRAFANA_API_KEY", value = var.grafana_api_key }
      ]

      portMappings = []
      mountPoints = []
      volumesFrom = []
      systemControls = []
    }
  ])

  tags = merge(var.tags, { Name = "${var.project}-task" })
}

# ── ECS Service ───────────────────────────────────────────────────────────────
resource "aws_ecs_service" "pqr" {
  name            = "${var.project}-${var.environment}-service"
  cluster         = var.cluster_id
  task_definition = aws_ecs_task_definition.pqr.arn
  desired_count   = var.desired_count
  launch_type = "FARGATE"

  # Permite actualizaciones sin downtime
  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups = [var.ecs_security_group_id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = var.target_group_arn
    container_name   = "pqr-management"
    container_port   = var.app_port
  }

  lifecycle {
    ignore_changes = [task_definition, desired_count]
  }

  tags = var.tags
}
