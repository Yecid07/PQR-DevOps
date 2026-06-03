output "service_name"   { value = kubernetes_service.pqr.metadata[0].name }
output "namespace"      { value = kubernetes_namespace.pqr.metadata[0].name }
output "ingress_name"   { value = kubernetes_ingress_v1.pqr.metadata[0].name }