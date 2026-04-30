resource "aws_key_pair" "bastion" {
  key_name   = "${var.project}-${var.environment}-bastion-key"
  public_key = file(var.public_key_path)
  tags       = var.tags
}

resource "aws_eip" "bastion" {
  domain = "vpc"
  tags   = merge(var.tags, { Name = "${var.project}-${var.environment}-bastion-eip" })
}

resource "aws_instance" "bastion" {
  ami                         = data.aws_ami.amazon_linux.id
  instance_type               = var.instance_type
  subnet_id                   = var.public_subnet_id
  key_name                    = aws_key_pair.bastion.key_name
  vpc_security_group_ids = [var.bastion_security_group_id]
  associate_public_ip_address = true

  # Script de inicio: instala psql para poder conectarse directamente al RDS
  user_data = <<-EOF
    #!/bin/bash
    yum update -y
    yum install -y postgresql15
    echo "Bastion listo. Conectate con: ssh -i <tu-key.pem> ec2-user@$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)" >> /etc/motd
  EOF

  tags = merge(var.tags, { Name = "${var.project}-${var.environment}-bastion" })
}

resource "aws_eip_association" "bastion" {
  instance_id   = aws_instance.bastion.id
  allocation_id = aws_eip.bastion.id
}

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }

  filter {
    name   = "state"
    values = ["available"]
  }
}
