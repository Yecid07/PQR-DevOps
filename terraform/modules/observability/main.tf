resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/ecs/${var.project}"
  retention_in_days = 30
  tags              = var.tags
}

resource "aws_cloudwatch_dashboard" "pqr" {
  dashboard_name = "${var.project}-${var.environment}"

  dashboard_body = jsonencode({
    widgets = [
      {
        type = "metric"
        properties = {
          title  = "ECS CPU Utilization"
          region = var.aws_region
          metrics = [["AWS/ECS", "CPUUtilization",
                      "ClusterName", "${var.project}-${var.environment}"]]
          period = 60
          stat   = "Average"
        }
      },
      {
        type = "metric"
        properties = {
          title  = "ECS Memory Utilization"
          region = var.aws_region
          metrics = [["AWS/ECS", "MemoryUtilization",
                      "ClusterName", "${var.project}-${var.environment}"]]
          period = 60
          stat   = "Average"
        }
      }
    ]
  })
}
