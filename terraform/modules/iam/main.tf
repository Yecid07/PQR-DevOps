data "aws_iam_policy_document" "ecs_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

# ── Execution Role (pull imagen, escribir logs) ───────────────────────────────
resource "aws_iam_role" "ecs_execution" {
  name               = "${var.project}-${var.environment}-ecs-execution-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_assume_role.json
  tags               = var.tags
}

resource "aws_iam_role_policy_attachment" "ecs_execution_managed" {
  role       = aws_iam_role.ecs_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Permiso extra: leer de ECR privado (ya incluido en managed policy, pero explícito)
resource "aws_iam_role_policy" "ecr_pull" {
  name = "${var.project}-${var.environment}-ecr-pull"
  role = aws_iam_role.ecs_execution.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["ecr:GetDownloadUrlForLayer", "ecr:BatchGetImage",
                  "ecr:BatchCheckLayerAvailability", "ecr:GetAuthorizationToken"]
      Resource = "*"
    }]
  })
}

# ── Task Role (permisos en runtime) ──────────────────────────────────────────
resource "aws_iam_role" "ecs_task" {
  name               = "${var.project}-${var.environment}-ecs-task-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_assume_role.json
  tags               = var.tags
}

resource "aws_iam_role_policy" "ecs_task_cloudwatch" {
  name = "${var.project}-${var.environment}-task-cw"
  role = aws_iam_role.ecs_task.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["logs:CreateLogStream", "logs:PutLogEvents",
                  "cloudwatch:PutMetricData", "xray:PutTraceSegments"]
      Resource = "*"
    }]
  })
}
