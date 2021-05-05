# Production Readiness

Albeit just a sample project, follows a non-exhaustive list of tasks grouped in categories of what needs to be done
before putting this project in production.

## Security

- [ ] Authentication and authorization.
- [ ] Remove/redact PII information from the logs.
- [ ] Review if all unhappy paths are not leaking internal information.

## Monitoring

- [ ] Monitoring infrastructure (e.g., Grafana for Telemetry and Elastic stack for logging).
- [ ] Review logging to ensure that we have the minimal business information available to reproduce bugs offline.
- [ ] Nice to have: structured logging to ease log filtering (e.g., by employee id).
- [ ] Nice to have: Distributed tracing between the HTTP client and the server requests. Eases debugging.

## Performance

After proper benchmarks with the typical workload on the real environment (we can only improve once we have a baseline to compare with):
- [ ] Batch insertions on the `shift` table.
- [ ] Index on the `shift` table over the start/end date times columns (trading reads for writes).
- [ ] Index on the `shift` table over the employee id column (trading reads for writes).
- [ ] Consider partitioning the database by timestamp to reduce the scan area (trading availability for consistency).
- [ ] No data retention policy (for simplicity).

## Deployment

The application is dockerized. In order to make this available in Kubernetes:
- [ ] Ensure that we have a Kubernetes Cluster ready.
- [ ] Setup PostGreSQL infrastructure including segregated users/roles and other sensible settings (including superuser).
- [ ] Setup [Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/) for vanity URLs.
- [ ] [Rolling updates](https://www.bluematador.com/blog/kubernetes-deployments-rolling-update-configuration) to ensure consistent release versions on multiple pods during upgrades.
- [ ] Review how secrets are managed as they currently live in the application's `ConfigMap`. I would explore [Vault](https://www.vaultproject.io/) as it is being recommended in the community.
- [ ] Segregate the deployment depending on the environments (and optionally availability zones).
- [ ] Improve deployment procedure. I would explore [Terraform](https://www.terraform.io/) as it is being recommended in the community.

## Internal

- [ ] Improve API-Docs. Atm is good enough. It is better than documenting the API manually.
- [ ] Continuous delivery pipeline that deploys as well on their respective environments.
