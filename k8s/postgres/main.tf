# Local Kubernetes. One should point to a remote cluster.
provider "kubernetes" {
  config_path = "~/.kube/config"
}

# Setup Persistent Volume Locally. One should point to a remote resource.
# https://registry.terraform.io/providers/hashicorp/kubernetes/latest/docs/resources/persistent_volume
resource "kubernetes_persistent_volume" "persistent_volume_example" {
  metadata {
    name = "postgres-pv-volume"
  }
  spec {
    capacity = {
      storage = "2Gi"
    }
    access_modes = ["ReadWriteMany"]
    persistent_volume_source {
      vsphere_volume {
        volume_path = "/mnt/data"
      }
    }
  }
}

# See https://registry.terraform.io/providers/cyrilgdn/postgresql/latest/docs
provider "postgresql" {
  host            = "127.0.0.1"
  port            = 5432
  database        = "${var.environment}_${var.role}"
  username        = "postgres"
  password        = "some-secure-password" # Maybe we can leverage some tool to fetch the passwords from a secure storage while keeping this git secured.
  sslmode         = "require"
  connect_timeout = 15
}
