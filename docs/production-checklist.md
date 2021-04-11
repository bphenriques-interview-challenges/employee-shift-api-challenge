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
- [ ] Consider partitioning the database by timestamp to reduce the scan area (trading availability for consistency).

## Containerization

The application is dockerized. The only thing missing is setting up properly the Kubernetes environment as it has some missing pieces. For example:
- [Rolling updates](https://www.bluematador.com/blog/kubernetes-deployments-rolling-update-configuration) to ensure consistent release versions on multiple pods during upgrades.
- Proper Postgres setup including segregated users/roles and other sensible settings (including superuser).
- Store deployment secrets securely (they are in plain-text and version controller). For example [Vault](https://www.vaultproject.io/).
- Segregate the deployment depending on the environments and/or availability zones.
- Readable URL (using [Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/)).

Would like to explore [Terraform](https://www.terraform.io/) as it is being recommended in the community.

# Tech Debt

Follows some nice-to-have non-functional requirements:
- Continuous delivery pipeline.
- Monitoring infrastructure (e.g., Grafana for Telemetry and Elastic stack for logging).
- Improve API-Docs. Atm is good enough. It is better than documenting the API manually.
- Structured logging to ease log filtering (e.g., by employee id).
- There is no data retention policy (for simplicity).
- Distributed tracing between the HTTP client and the server requests. Eases debugging.

# Additional Comments

## On Hexagonal Architecture

Hexagonal architecture is indeed interesting but does not provide many benefits on a small project as one changes requires
going through all the different layers/modules. On large projects where responsibilities can become fuzzy, I see it as
great tool to promote decoupled components.

## On leveraging DB for business rules

Moreover, I leveraged PostgreSQL constraints as means to ensure that the business rules were being met (e.g., uniqueness
and avoid shift overlap). The Hexagonal architecture practices suggest that these rules should be implemented on the domain
level instead. I kinda disagree as it would make the project far more complex, and I prefer having these rules as close as possible
from the source of truth. Being "pure" would require more I/O requests to the database querying if the information is valid
and transactions to ensure that the "checks" and "updates" were atomic, otherwise we could be inserting new entries that
would violate the business rules.

## Reactive

This project explored WebFlux and Reactive Databases. There are still some quirks, e.g., the laziness of JSON deserialization
which leads to weird issues and the lack of input validation on the controller level without materializing the whole stream. As such,
I replaced the reactive database for the regular one.

Lastly, I will continue to use Webflux with co-routines as long as I know there are I/O operations, otherwise the benefits are minimal. However,
as with every benchmarking questions... just benchmark it!
