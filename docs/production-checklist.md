# Production Readiness

Follows the set of tasks that needs to be done before going into production:
- [ ] Authentication and authorization.
- [ ] CI/CD plus deployment instructions. Dummy guide to avoid stress when things go wrong.
- [ ] Making available the application through readable URL.
- [ ] Remove/redact PII information from the logs.
- [ ] Review if all unhappy paths are not leaking internal information.
- [ ] Proper setup of the Database: segregated users/roles and other sensible settings.

After proper benchmarks after knowing the typical workload (we need measurements before making any improvement):
- [ ] Batch insertions on the `shift` table.
- [ ] Index on the `shift` table over the start/end date times columns (trading reads for writes).
- [ ] Index on the `shift` table over the employee id column (trading reads for writes).
- [ ] Consider partitioning the database by timestamp to reduce the scan area (high availability).

# Tech Debt

Non-functional requirements:
- K8S setup lack [rolling updates](https://www.bluematador.com/blog/kubernetes-deployments-rolling-update-configuration) to ensure consistent release versions on multiple pods during upgrades.
- K8S deployment lack of proper management of secrets (they are being version-controlled).
- K8S deployment should branch depending on the environments and/or availability zones.
- Monitoring infrastructure (e.g., Grafana for Telemetry and Elastic stack for logging).
- Improve API-Docs. Atm is good enough. It is better than documenting the API manually.
- Structured logging to ease log filtering (e.g., by employee id).
- There is no data retention policy (for simplicity).

Nice to have:
- Distributed tracing between the HTTP client and the server requests. Eases debugging.
