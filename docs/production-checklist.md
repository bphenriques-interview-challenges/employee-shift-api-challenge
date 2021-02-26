# Production Readiness

Follows the set of tasks that needs to be done before going into production:
- [ ] Authentication and authorization.
- [ ] Deployment Instructions. Dummy guide.
- [ ] Review PII information that we may need to redact before making using production data.
- [ ] Review API Error responses and omit any internal information (not all unhappy paths are filtered properly).

After proper benchmarks (we need measurements before making any improvement):
- [ ] Consider partitioning the database by timestamp to reduce the scan area when looking for shifts.

# Tech Debt

Non-functional requirements:
* Logging Infrastructure to be able to store and review logs.
* Structured logging to be easily filter log entries.
* Useful: Given that an API does not live by itself, it is useful having trace logging to ease debugging.
