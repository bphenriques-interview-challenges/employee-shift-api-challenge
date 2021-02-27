# Deployment to K8S

I usually have this provided by SRE. Follows my first experiments with deploying Postgres manually. Most of this should
be automated using [terraform](https://www.terraform.io/).

Leveraging [terraform](https://www.terraform.io/) for server provisioning and immutable deployments.

# Requirements

1. Running local Kubernetes cluster. If you are using macOS follow this [guide](https://docs.docker.com/docker-for-mac/#kubernetes).
2. [Terraform](https://www.terraform.io/) installed.

# Deploying

## Postgres

In the `postgres` folder:

Load the plugins:
```
$  terraform init 
```

Preview the changes:
```sh
$ terraform plan
```

Deploy the changes:
```sh
$ terraform apply
```

After the deployment is done, you can check the running pods:
```sh
$ kubectl get pod
```

And then check the service port:
```sh
$ kubectl get svc <service> -o jsonpath='{.spec.ports[0].nodePort}'
```

# Reference pages:

Follows some links I found useful during this setup:
- https://www.terraform.io/intro/index.html
- https://www.reddit.com/r/kubernetes/comments/i9l17t/helm_vs_terraform/  
- https://blog.gruntwork.io/why-we-use-terraform-and-not-chef-puppet-ansible-saltstack-or-cloudformation-7989dad2865c
- https://severalnines.com/database-blog/using-kubernetes-deploy-postgresql
- https://stackoverflow.com/questions/58481850/no-matches-for-kind-deployment-in-version-extensions-v1beta1
