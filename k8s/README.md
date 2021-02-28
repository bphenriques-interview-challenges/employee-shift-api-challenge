# Deployment to K8S

Follows my experiment on deploying this manually in the local K8S cluster. This is usually provided by SRE so this is 
good experiment. It is already possible to deploy the docker image in the root of the project using the `run` docker-compose
target, however it does not take advantage of the health probes used in K8S.

# Requirements

- A running local Kubernetes cluster. If you are using macOS follow this [guide](https://docs.docker.com/docker-for-mac/#kubernetes).
- The docker image `bphenriques/employee-shifts-api:latest` but be present in your docker registry.

# Deploying

Deploy everything:
```
$ make deploy
```

After the deployment is done, you can check the status of the existing pods:
```sh
$ kubectl get deployments
NAME                  READY   UP-TO-DATE   AVAILABLE   AGE
employee-shifts-api   2/2     2            2           115s
postgres              1/1     1            1           115s
```

Once you see this the status `Running` and `READY` set to `1/1` you may use the API:
- **Swagger UI**: http://localhost:30001/swagger-ui.html
- **Readiness probe**: http://localhost:30002/actuator/health/readiness
- **Liveness probe**: http://localhost:30002/actuator/health/liveness
- **Prometheus scrape**: http://localhost:30002/actuator/prometheus

Note: The port `30002` is exposed outside solely for demonstration purposes. It should be hidden in other environments.

Do some requests and check the logs:
```sh
$ kubectl logs -f employee-shifts-api-b46ff4745-jwl44
```

# Tearing down
Run the following command:
```
$ make delete
```

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
