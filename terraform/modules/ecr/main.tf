# ── Repositorio ECR: aplicación Spring Boot ──────────────────────────────────
resource "aws_ecr_repository" "app" {
  name                 = "${var.project}-management"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = merge(var.tags, { Name = "${var.project}-management" })
}

# ── Repositorio ECR: Grafana Alloy personalizado ──────────────────────────────
resource "aws_ecr_repository" "alloy" {
  name                 = "alloy-custom"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = merge(var.tags, { Name = "alloy-custom" })
}

# ── Política de ciclo de vida: mantener últimas 5 imágenes ───────────────────
resource "aws_ecr_lifecycle_policy" "app" {
  repository = aws_ecr_repository.app.name
  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Mantener las ultimas 5 imagenes"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 5
      }
      action = { type = "expire" }
    }]
  })
}

resource "aws_ecr_lifecycle_policy" "alloy" {
  repository = aws_ecr_repository.alloy.name
  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Mantener las ultimas 5 imagenes"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 5
      }
      action = { type = "expire" }
    }]
  })
}
