prometheus.scrape "spring_boot" {
  targets = [
    {
      __address__ = "pqr-management:8080",
    },
  ]

  metrics_path = "/actuator/prometheus"

  scrape_interval = "15s"

  forward_to = [
    prometheus.remote_write.grafana.receiver,
  ]
}

prometheus.remote_write "grafana" {
  endpoint {
    url = "https://prometheus-prod-66-prod-us-east-3.grafana.net/api/prom/push"

   basic_auth {
     username = "3116196"
     password = env("GRAFANA_API_KEY")
   }
  }
}