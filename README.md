# Employee Shift API

The challenge description is available [here](docs/challenge.md).

The application explores the [Hexagonal](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) architecture 
leveraging Spring-Boot's dependency injection. Moreover, the application is built around the Reactive principles using:
- [Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) as the HTTP interface.
- [Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc) to interact with the databases.
- [Flyway](https://flywaydb.org/) to automate migrations and because you likely won't have *direct* access to the DB in production.

You may find some notes on production readiness [here](docs/production-checklist.md).

# Development

Requirements:
- Java 11

## Building

Docker image:
```
$ make build
```

Locally:
```
$ make build-local
```

## Running

Dockerized:
```sh
$ make run
```

Locally:
```sh
$ make run-local
```

In any case, the API-Docs are available at http://localhost:8080/swagger-ui.html.

There are three additional endpoints on port `8081` which is specific for monitoring:
- **Liveness Probe**: localhost:8081/actuator/health/liveness
- **Readiness Probe**: localhost:8081/actuator/health/readiness
- **Prometheus Metrics**: localhost:8081/actuator/prometheus

## Testing

As it runs in the continuous-integration:
```sh
$ make test
```

In order to run locally (for speed at the expense of slight inaccuracy):
```sh
$ make test-dependencies-up
```

You may now run the `test` gradle target or directly in IntelliJ.

## Nail polish

Linter:
```sh
$ make lint
```

Format:
```sh
$ make format
```

## Project Structure

* `domain`: Business rules.
* `infrastructure`: Interaction with the infrastructure (Database).
* `web-app`: Exposes HTTP interface.
* `common-test`: As the name says.
* `db`: Data migrations scripts.
* `docs`: Relevant documentation.
* `ci`: Continuous integrations scripts.
* `buildSrc` and `gradle`: Relevant folders to build the project itself.
