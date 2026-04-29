terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # # Estado remoto en S3 + lock en DynamoDB
  # backend "s3" {
  #   bucket         = "pqr-terraform-state"
  #   key            = "global/terraform.tfstate"
  #   region         = "us-east-2"
  #   dynamodb_table = "pqr-terraform-lock"
  #   encrypt        = true
  # }
}

provider "aws" {
  region = var.aws_region
}
