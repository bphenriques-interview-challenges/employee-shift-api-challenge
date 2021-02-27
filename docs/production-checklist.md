# Production Readiness

Follows the set of tasks that needs to be done before going into production:
- [ ] Authentication and authorization.
- [ ] Deployment Instructions. Dummy guide.
- [ ] Review PII information that we may need to redact before making using production data.
- [ ] Review API Error responses.
- [ ] Omit any internal information as not all unhappy paths are handled.

After proper benchmarks (we need measurements before making any improvement):
- [ ] Consider partitioning the database by timestamp to reduce the scan area when looking for shifts.

# Tech Debt

Non-functional requirements:
- Does not validate if the upserted shifts have duplicates. It shouldn't cause any problems.
- Unfortunately constraint validations errors opaque and can't.
- Logging Infrastructure to be able to store and review logs offline.
- Structured logging to ease log filtering (e.g., by employee id).
  
Nice to have:
- Distributed tracing between the HTTP client and the server requests. Eases debugging.
