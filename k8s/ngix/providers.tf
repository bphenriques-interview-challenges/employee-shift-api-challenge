terraform {
  required_version = "~> 0.13"

  # https://registry.terraform.io/providers/hashicorp/kubernetes/latest/docs/guides/getting-started#creating-your-first-kubernetes-resources
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = ">= 2.0.0"
    }
  }
}
