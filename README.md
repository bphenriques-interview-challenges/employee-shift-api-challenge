# Employee Shift API

The challenge description is available [here](docs/challenge.md).

# Architecture

The application explores the [Hexagonal](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) architecture 
using Spring-Boot's dependency injection.

# Development

Requirements:
- Java 11

## Running

Locally:
```sh
$ make run
```

Dockerized:
```sh
$ make run-dockerized
```

The API-Docs are available at http://localhost:8080/swagger-ui.html.

There are three additional endpoints on port `8081` which is specific for monitoring:
- *Liveness Probe*: localhost:8081/actuator/health/liveness
- *Readiness Probe*: localhost:8081/actuator/health/readiness
- *Prometheus Metrics*: localhost:8081/actuator/prometheus

PS: I am lazy. I prefer automatic documentation. Is easier that manually writing.

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
