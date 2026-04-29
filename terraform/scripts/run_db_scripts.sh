#!/usr/bin/env bash
# =============================================================
# run_db_scripts.sh
# Conexión a RDS a través del Bastion Host via túnel SSH
#
# Uso:
#   ./run_db_scripts.sh <BASTION_IP> <RDS_ENDPOINT> <KEY.pem> [all|create|seed|drop]
#
# Ejemplo:
#   ./run_db_scripts.sh 3.14.12.99 pqr-dev.xyz.us-east-2.rds.amazonaws.com ~/.ssh/pqr-key.pem all
# =============================================================

set -euo pipefail

BASTION_IP="${1:-}"
RDS_ENDPOINT="${2:-}"
SSH_KEY="${3:-}"
ACTION="${4:-all}"

LOCAL_PORT="5433"
DB_USER="postgres"
DB_NAME="postgres"
SCRIPT_DIR="$(cd "$(dirname "$0")/db" && pwd)"

if [[ -z "$BASTION_IP" || -z "$RDS_ENDPOINT" || -z "$SSH_KEY" ]]; then
  echo "Uso: $0 <BASTION_IP> <RDS_ENDPOINT> <KEY.pem> [all|create|seed|drop]"
  echo ""
  echo "Ejemplo:"
  echo "  $0 3.14.12.99 pqr-dev.xyz.us-east-2.rds.amazonaws.com ~/.ssh/pqr-key.pem all"
  exit 1
fi

export PGPASSWORD="${DB_PASSWORD:-YOkiHP79h8tWtKwx0gAS}"

# ── Abrir túnel SSH en background ────────────────────────────────────────────
echo ""
echo "============================================"
echo "  Abriendo túnel SSH..."
echo "  Bastion : $BASTION_IP"
echo "  RDS     : $RDS_ENDPOINT"
echo "  Puerto  : localhost:$LOCAL_PORT -> $RDS_ENDPOINT:5432"
echo "============================================"

ssh -i "$SSH_KEY" \
    -L "${LOCAL_PORT}:${RDS_ENDPOINT}:5432" \
    -o StrictHostKeyChecking=no \
    -o ExitOnForwardFailure=yes \
    -fN \
    "ec2-user@${BASTION_IP}"

echo "  Tunel abierto correctamente."
sleep 2

run_script() {
  local file="$1"
  echo ""
  echo "============================================"
  echo "  Ejecutando: $(basename "$file")"
  echo "============================================"
  psql -h localhost -p "$LOCAL_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$file"
}

case "$ACTION" in
  create) run_script "$SCRIPT_DIR/01_create.sql" ;;
  seed)   run_script "$SCRIPT_DIR/02_seed.sql" ;;
  drop)   run_script "$SCRIPT_DIR/03_drop.sql" ;;
  all)
    run_script "$SCRIPT_DIR/01_create.sql"
    run_script "$SCRIPT_DIR/02_seed.sql"
    echo ""
    echo ">>> Para hacer drop: $0 $BASTION_IP $RDS_ENDPOINT $SSH_KEY drop"
    ;;
  *)
    echo "Accion no valida. Usar: all | create | seed | drop"
    exit 1
    ;;
esac

echo ""
echo ">>> Cerrando tunel SSH..."
TUNNEL_PID=$(pgrep -f "L ${LOCAL_PORT}:${RDS_ENDPOINT}" 2>/dev/null || true)
if [[ -n "$TUNNEL_PID" ]]; then
  kill "$TUNNEL_PID"
  echo "    Tunel cerrado (PID $TUNNEL_PID)."
fi

echo ""
echo "Completado."
