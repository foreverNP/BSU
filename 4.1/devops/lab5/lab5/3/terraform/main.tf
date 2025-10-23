terraform {
  required_providers {
    null = {
      source  = "hashicorp/null"
      version = "~> 3.0"
    }
  }
}

variable "use_minikube" {
  type    = bool
  default = true
}

variable "minikube_nodes" {
  type    = number
  default = 3
}

resource "null_resource" "create_cluster" {
  triggers = {
    use_minikube = tostring(var.use_minikube)
    when         = timestamp()
  }

  provisioner "local-exec" {
    interpreter = ["/bin/bash", "-c"]
    command     = <<EOT
set -e
echo "Starting minikube (nodes=${var.minikube_nodes})..."
if ! command -v minikube >/dev/null 2>&1; then
  echo "minikube not found. Please install minikube."
  exit 1
fi

PROFILE="tf-minikube"
minikube start --driver=docker --nodes ${var.minikube_nodes} -p $${PROFILE}

echo "Minikube profile '$${PROFILE}' started with ${var.minikube_nodes} nodes."
kubectl config use-context "minikube" || true
kubectl get nodes -o wide
EOT
  }
}
