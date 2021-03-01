# Production Readiness

Follows a non-exhaustive list of tasks that needs to be reviewed before going into production.

## Backend service

- [ ] Authentication and authorization.
- [ ] Remove/redact PII information from the logs.
- [ ] Review if all unhappy paths are not leaking internal information.

After proper benchmarks with the typical workload on the real environment (we can only improve once we have a baseline to compare with):
- [ ] Batch insertions on the `shift` table.
- [ ] Index on the `shift` table over the start/end date times columns (trading reads for writes).
- [ ] Index on the `shift` table over the employee id column (trading reads for writes).
- [ ] Consider partitioning the database by timestamp to reduce the scan area (high availability).

## Containerization

The application is dockerized. The only thing missing is setting up properly the Kubernetes environment as it has some missing pieces. For example:
- [Rolling updates](https://www.bluematador.com/blog/kubernetes-deployments-rolling-update-configuration) to ensure consistent release versions on multiple pods during upgrades.
- Proper Postgres setup including segregated users/roles and other sensible settings (including superuser).
- Store deployment secrets securely (they are in plain-text and version controller). For example [Vault](https://www.vaultproject.io/).
- Segregate the deployment depending on the environments and/or availability zones.
- Readable URL (using [Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/)).

The company I work at uses [Terraform](https://www.terraform.io/) which is something to consider. Was interesting when
I tried it.

# Tech Debt

Follows some nice-to-have non-functional requirements:
- Continuous delivery pipeline.
- Monitoring infrastructure (e.g., Grafana for Telemetry and Elastic stack for logging).
- Improve API-Docs. Atm is good enough. It is better than documenting the API manually.
- Structured logging to ease log filtering (e.g., by employee id).
- There is no data retention policy (for simplicity).
- Distributed tracing between the HTTP client and the server requests. Eases debugging.
