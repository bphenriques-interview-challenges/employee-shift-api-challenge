# Production Readiness

Follows the set of tasks that needs to be done before going into production:
- [ ] Authentication and authorization.
- [ ] CI/CD plus deployment Instructions. Dummy guide to avoid stress when things go wrong.
- [ ] Remove/redact PII information from the logs.
- [ ] Review API Error responses.
- [ ] Review if all unhappy paths are not leaking internal information.
- [ ] Monitoring infrastructure (e.g., Grafana for Telemetry and Elastic stack for logging).
- [ ] Proper setup of the Database: segregated users/roles and other sensible settings.

After proper benchmarks (we need measurements before making any improvement):
- [ ] Consider creating an index on the `shift` table over the start/end date times columns.
- [ ] Consider creating an index on the `shift` table over the employee id column.
- [ ] Consider partitioning the database by timestamp to reduce the scan area.

# Tech Debt

Non-functional requirements:
- Unfortunately constraint validations errors opaque and is hard to provide helpful information to caller.
- API-Docs are good enough atm for demonstration. I am lazy and dislike manual (architecture) documentation.
- Structured logging to ease log filtering (e.g., by employee id).
- The information will remain in the DB permanently for simplicity.

Nice to have:
- Distributed tracing between the HTTP client and the server requests. Eases debugging.
