# Terraform — Proyecto PQR

Infraestructura modular en AWS para la aplicación **PQR Management** con Spring Boot, ECS Fargate, RDS PostgreSQL, Bastion Host, Grafana Alloy y CloudWatch.

---

## Arquitectura

```
Internet
   │
   ├──► ALB (subnet pública)
   │         │
   │         └──► ECS Fargate (subnet privada)
   │                   pqr-management + grafana-alloy sidecar
   │                         │
   │                         └──► RDS PostgreSQL (subnet privada)
   │
   └──► Bastion Host EC2 (subnet pública)
              │  SSH tunnel
              └──────────────► RDS PostgreSQL :5432
```

**Servicios aprovisionados:**

| # | Módulo | Servicio AWS |
|---|--------|-------------|
| 1 | `ecr` | ECR — repositorios app + alloy |
| 2 | `network` | VPC, subnets, IGW, NAT, Security Groups |
| 3 | `bastion` | EC2 Bastion Host + Elastic IP |
| 4 | `rds` | RDS PostgreSQL 15 (subnet privada) |
| 5 | `load-balancer` | Application Load Balancer |
| 6 | `ecs` | ECS Cluster con Fargate |
| 7 | `fargate-task` | Task Definition + ECS Service |
| 8 | `iam` | Roles de ejecución |
| 9 | `observability` | CloudWatch Log Group + Dashboard |

---

## Prerrequisitos

```bash
terraform -version   # >= 1.5
aws configure        # credenciales configuradas
psql --version       # cliente PostgreSQL
ssh -V               # cliente SSH
```

---

## Generar el par de claves SSH (una sola vez)

```bash
ssh-keygen -t rsa -b 4096 -f ~/.ssh/pqr-key -N ""
# Genera: ~/.ssh/pqr-key (privada) y ~/.ssh/pqr-key.pub (pública)
```

---

## Despliegue

### 1. Inicializar

```bash
cd pqr/terraform-pqr
terraform init
```

### 2. Plan

```bash
terraform plan -var-file=environments/dev/terraform.tfvars \
               -var="bastion_public_key_path=~/.ssh/pqr-key.pub"
```

### 3. Aplicar

```bash
terraform apply -var-file=environments/dev/terraform.tfvars \
                -var="bastion_public_key_path=~/.ssh/pqr-key.pub"
```

Al terminar, Terraform imprime:

```
bastion_public_ip      = "3.14.12.99"
bastion_ssh_command    = "ssh -i ~/.ssh/pqr-key.pem ec2-user@3.14.12.99"
bastion_tunnel_command = "ssh -i ~/.ssh/pqr-key.pem -L 5433:pqr-dev.xxx.us-east-2.rds.amazonaws.com:5432 ec2-user@3.14.12.99 -N"
rds_endpoint           = "pqr-dev.xxx.us-east-2.rds.amazonaws.com"
app_url                = "http://pqr-dev-alb-xxx.us-east-2.elb.amazonaws.com/api/v1"
```

---

## Conexión a la base de datos via Bastion Host

### Paso 1 — Abrir el túnel SSH

```bash
ssh -i ~/.ssh/pqr-key.pem \
    -L 5433:pqr-dev.xxx.us-east-2.rds.amazonaws.com:5432 \
    ec2-user@<BASTION_IP> \
    -N &
```

El flag `-N` deja el túnel abierto en background sin abrir shell.

### Paso 2 — Conectarse a RDS a través del túnel

```bash
psql -h localhost -p 5433 -U postgres -d postgres
# Contraseña: YOkiHP79h8tWtKwx0gAS
```

---

## Scripts de base de datos

### Automático (abre y cierra el túnel solo)

```bash
chmod +x scripts/run_db_scripts.sh

# Crear tablas
./scripts/run_db_scripts.sh <BASTION_IP> <RDS_ENDPOINT> ~/.ssh/pqr-key.pem create

# Insertar datos y visualizar
./scripts/run_db_scripts.sh <BASTION_IP> <RDS_ENDPOINT> ~/.ssh/pqr-key.pem seed

# Drop completo
./scripts/run_db_scripts.sh <BASTION_IP> <RDS_ENDPOINT> ~/.ssh/pqr-key.pem drop

# Los tres en orden (create + seed)
./scripts/run_db_scripts.sh <BASTION_IP> <RDS_ENDPOINT> ~/.ssh/pqr-key.pem all
```

### Manual (con túnel abierto en Paso 1)

```bash
export PGPASSWORD="YOkiHP79h8tWtKwx0gAS"

psql -h localhost -p 5433 -U postgres -d postgres -f scripts/db/01_create.sql
psql -h localhost -p 5433 -U postgres -d postgres -f scripts/db/02_seed.sql
psql -h localhost -p 5433 -U postgres -d postgres -f scripts/db/03_drop.sql
```

---

## Destruir la infraestructura

```bash
terraform destroy -var-file=environments/dev/terraform.tfvars -auto-approve
```

---

## Estructura de archivos

```
terraform-pqr/
├── main.tf
├── variables.tf
├── outputs.tf
├── provider.tf
├── modules/
│   ├── bastion/        # EC2 Bastion Host + Elastic IP + Key Pair
│   ├── ecr/            # Repositorios Docker (app + alloy)
│   ├── network/        # VPC, subnets, SGs (incluye SG Bastion)
│   ├── rds/            # PostgreSQL en subnet privada
│   ├── ecs/            # Cluster ECS
│   ├── fargate-task/   # Task Definition + Service
│   ├── load-balancer/  # ALB + Target Group
│   ├── iam/            # Roles de ejecución
│   └── observability/  # CloudWatch Logs + Dashboard
├── environments/
│   └── dev/
│       ├── main.tf
│       └── terraform.tfvars
└── scripts/
    ├── run_db_scripts.sh   # Abre túnel SSH y ejecuta SQLs
    └── db/
        ├── 01_create.sql
        ├── 02_seed.sql
        └── 03_drop.sql
```

---
