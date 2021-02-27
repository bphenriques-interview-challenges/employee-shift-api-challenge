# TODO: https://upcloud.com/community/stories/terraform-kubernetes-provider/
resource "kubernetes_pod" "nginx" {
  metadata {
    name = "nginx-example"
    labels {
      App = "nginx"
    }
  }

  spec {
    container {
      image = "nginx:1.15.2"
      name  = "example"

      port {
        container_port = 80
      }
    }
  }
}

resource "kubernetes_service" "nginx" {
  metadata {
    name = "nginx-example"
  }
  spec {
    selector {
      App = kubernetes_pod.nginx.metadata[0].labels.App
    }
    port {
      port = 80
      target_port = 80
    }

    type = "LoadBalancer"
  }
}

output "lb_ip" {
  value = kubernetes_service.nginx.load_balancer_ingress[0].ip
}
