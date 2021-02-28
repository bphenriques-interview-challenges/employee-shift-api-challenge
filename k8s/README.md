# Deployment to K8S

Follows my experiment on deploying this manually in the local K8S cluster. This is usually provided by SRE so this is 
good experiment. It is already possible to deploy the docker image in the root of the project using the `run` docker-compose
target, however it does take advantage of the health probes used in K8S.

Last and not the least, one should have the server automated regardless of the environment (e.g., `staging`, `production`) or
availability zone (e.g., `us-east1`).

# Requirements

A running local Kubernetes cluster. If you are using macOS follow this [guide](https://docs.docker.com/docker-for-mac/#kubernetes).

# Deploying

Deploy everything:
```
$ make deploy
```

After the deployment is done, you can check the running pods:
```sh
$ kubectl get pod
```

You can service port of the api:
```sh
$ kubectl get svc employee-shifts-api
```

# Tearing down
Run the following command:
```
$ make delete
```

K8S can be moody and it 

# Future work

Once I get the hang of the foundations I would like to research [terraform](https://www.terraform.io/)
which I started in branch `terraform` which avoids coordinating manually the deployments while allowing having the
infrastructure explicit through code (and having immutable deployments).

# Reference pages:

Follows some links I found useful during this setup:
- https://circleci.com/blog/learn-iac-part1/
- https://www.terraform.io/intro/index.html
- https://www.reddit.com/r/kubernetes/comments/i9l17t/helm_vs_terraform/  
- https://blog.gruntwork.io/why-we-use-terraform-and-not-chef-puppet-ansible-saltstack-or-cloudformation-7989dad2865c
- https://severalnines.com/database-blog/using-kubernetes-deploy-postgresql
- https://stackoverflow.com/questions/58481850/no-matches-for-kind-deployment-in-version-extensions-v1beta1
