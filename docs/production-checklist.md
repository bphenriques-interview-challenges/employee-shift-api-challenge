# Production Readiness

Follows the set of tasks that needs to be done before going into production:
- [ ] Authentication and authorization.
- [ ] Remove/redact PII information from the logs.
- [ ] CI/CD plus deployment instructions. Dummy guide to avoid stress when things go wrong.
- [ ] Review API Error responses.
- [ ] Review if all unhappy paths are not leaking internal information.
- [ ] Proper setup of the Database: segregated users/roles and other sensible settings.
- [ ] Split the infrastructure between environments (and if applicable availability zones).
- [ ] Making available the application through readable URL.

After proper benchmarks (we need measurements before making any improvement):
- [ ] Consider creating an index on the `shift` table over the start/end date times columns.
- [ ] Consider creating an index on the `shift` table over the employee id column.
- [ ] Consider partitioning the database by timestamp to reduce the scan area.

# Tech Debt

Non-functional requirements:
- K8S setup lack [rolling updates](https://www.bluematador.com/blog/kubernetes-deployments-rolling-update-configuration) to ensure consistent release versions on multiple pods during upgrades.
- K8S deployment lack of proper management of secrets (they are being version-controlled).
- Monitoring infrastructure (e.g., Grafana for Telemetry and Elastic stack for logging).
- Unfortunately constraint validations errors are opaque and is hard to provide helpful information to caller without manually parsing the String messages.
- API-Docs are good enough atm for demonstration. I am lazy and dislike manual (architecture) documentation.
- Structured logging to ease log filtering (e.g., by employee id).
- There is no data retention policy (for simplicity).

Nice to have:
- Distributed tracing between the HTTP client and the server requests. Eases debugging.
