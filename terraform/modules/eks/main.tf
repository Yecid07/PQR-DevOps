resource "aws_eks_cluster" "main" {
  name     = "${var.project}-${var.environment}"
  role_arn = var.eks_cluster_role_arn
  version  = "1.35"          #

  vpc_config {
    subnet_ids              = concat(var.public_subnets, var.private_subnets)
    endpoint_private_access = true
    endpoint_public_access  = true
    public_access_cidrs     = ["0.0.0.0/0"]
  }

  tags = var.tags
}
resource "aws_eks_node_group" "main" {
  cluster_name    = aws_eks_cluster.main.name
  node_group_name = "${var.project}-${var.environment}-nodes"
  node_role_arn   = var.eks_node_role_arn
  subnet_ids      = var.private_subnets
  instance_types  = ["t3.small"]

  scaling_config {
    desired_size = 1
    min_size     = 1
    max_size     = 2
  }

  update_config {
    max_unavailable = 1
  }

  tags = var.tags
}
# Tags requeridos por el ALB Controller para descubrir las subnets
resource "aws_ec2_tag" "public_subnet_alb" {
  count       = length(var.public_subnets)
  resource_id = var.public_subnets[count.index]
  key         = "kubernetes.io/role/elb"
  value       = "1"
}

resource "aws_ec2_tag" "private_subnet_alb" {
  count       = length(var.private_subnets)
  resource_id = var.private_subnets[count.index]
  key         = "kubernetes.io/role/internal-elb"
  value       = "1"
}

resource "aws_ec2_tag" "cluster_subnet_public" {
  count       = length(var.public_subnets)
  resource_id = var.public_subnets[count.index]
  key         = "kubernetes.io/cluster/${var.project}-${var.environment}"
  value       = "shared"
}

resource "aws_ec2_tag" "cluster_subnet_private" {
  count       = length(var.private_subnets)
  resource_id = var.private_subnets[count.index]
  key         = "kubernetes.io/cluster/${var.project}-${var.environment}"
  value       = "shared"
}

data "tls_certificate" "eks" {
  url = aws_eks_cluster.main.identity[0].oidc[0].issuer
}

resource "aws_iam_openid_connect_provider" "eks" {
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = [data.tls_certificate.eks.certificates[0].sha1_fingerprint]
  url             = aws_eks_cluster.main.identity[0].oidc[0].issuer
}
