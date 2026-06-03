# ── Namespace ──────────────────────────────────────────────
resource "kubernetes_namespace" "pqr" {
  metadata {
    name = var.namespace
  }
}

# ── Secret con credenciales de BD ─────────────────────────
resource "kubernetes_secret" "pqr_secrets" {
  metadata {
    name      = "pqr-secrets"
    namespace = kubernetes_namespace.pqr.metadata[0].name
  }

  data = {
    SPRING_DATASOURCE_URL      = var.db_url
    SPRING_DATASOURCE_USERNAME = var.db_username
    SPRING_DATASOURCE_PASSWORD = var.db_password
    GRAFANA_API_KEY            = var.grafana_api_key
    SERVICES_BOOK_ORDER_URL    = var.book_order_url
    PQR_BOOK_ORDER_THRESHOLD   = var.book_order_threshold
  }
}

# ── Deployment STABLE ──────────────────────────────────────
resource "kubernetes_deployment" "stable" {
  metadata {
    name      = "${var.project}-stable"
    namespace = kubernetes_namespace.pqr.metadata[0].name
    labels = {
      app     = var.project
      version = "stable"
    }
  }

  spec {
    replicas = var.stable_replicas

    selector {
      match_labels = {
        app     = var.project
        version = "stable"
      }
    }

    template {
      metadata {
        labels = {
          app     = var.project
          version = "stable"
        }
      }

      spec {
        container {
          name  = "${var.project}-app"
          image = var.stable_image

          port {
            container_port = var.app_port
          }

          env_from {
            secret_ref {
              name = kubernetes_secret.pqr_secrets.metadata[0].name
            }
          }

          env {
            name  = "SPRING_PROFILES_ACTIVE"
            value = "prod"
          }

          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = var.app_port
            }
            initial_delay_seconds = 60
            period_seconds        = 15
          }

          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = var.app_port
            }
            initial_delay_seconds = 30
            period_seconds        = 10
          }

          resources {
            requests = {
              cpu    = "250m"
              memory = "512Mi"
            }
            limits = {
              cpu    = "500m"
              memory = "1024Mi"
            }
          }
        }
      }
    }
  }
}

# ── Deployment CANARY ──────────────────────────────────────
resource "kubernetes_deployment" "canary" {
  metadata {
    name      = "${var.project}-canary"
    namespace = kubernetes_namespace.pqr.metadata[0].name
    labels = {
      app     = var.project
      version = "canary"
    }
  }

  spec {
    replicas = var.canary_replicas

    selector {
      match_labels = {
        app     = var.project
        version = "canary"
      }
    }

    template {
      metadata {
        labels = {
          app     = var.project
          version = "canary"
        }
      }

      spec {
        container {
          name  = "${var.project}-app"
          image = var.canary_image

          port {
            container_port = var.app_port
          }

          env_from {
            secret_ref {
              name = kubernetes_secret.pqr_secrets.metadata[0].name
            }
          }

          env {
            name  = "SPRING_PROFILES_ACTIVE"
            value = "prod"
          }

          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = var.app_port
            }
            initial_delay_seconds = 60
            period_seconds        = 15
          }

          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = var.app_port
            }
            initial_delay_seconds = 30
            period_seconds        = 10
          }

          resources {
            requests = {
              cpu    = "250m"
              memory = "512Mi"
            }
            limits = {
              cpu    = "500m"
              memory = "1024Mi"
            }
          }
        }
      }
    }
  }
}

# ── Service ÚNICO para ambos deployments ──────────────────
resource "kubernetes_service" "pqr" {
  metadata {
    name      = "${var.project}-service"
    namespace = kubernetes_namespace.pqr.metadata[0].name
  }

  spec {
    selector = {
      app = var.project
    }

    port {
      port        = 80
      target_port = var.app_port
    }

    type = "ClusterIP"
  }
}

# ── Ingress con weighted routing ──────────────────────────
resource "kubernetes_ingress_v1" "pqr" {
  metadata {
    name      = "${var.project}-ingress"
    namespace = kubernetes_namespace.pqr.metadata[0].name

    annotations = {
      "kubernetes.io/ingress.class"                            = "alb"
      "alb.ingress.kubernetes.io/scheme"                       = "internet-facing"
      "alb.ingress.kubernetes.io/target-type"                  = "ip"
      "alb.ingress.kubernetes.io/healthcheck-path"             = "/actuator/health"
      "alb.ingress.kubernetes.io/healthcheck-interval-seconds" = "15"
      "alb.ingress.kubernetes.io/healthy-threshold-count"      = "2"
      "alb.ingress.kubernetes.io/unhealthy-threshold-count"    = "3"
      "alb.ingress.kubernetes.io/success-codes"                = "200"
    }
  }

  spec {
    ingress_class_name = "alb"

    rule {
      http {
        path {
          path      = "/"
          path_type = "Prefix"

          backend {
            service {
              name = kubernetes_service.pqr.metadata[0].name
              port {
                number = 80
              }
            }
          }
        }
      }
    }
  }
}