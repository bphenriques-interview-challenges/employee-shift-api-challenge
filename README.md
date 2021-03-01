<img src="https://github.com/bphenriques-lab/employee-shift-api-challenge/actions/workflows/build.yaml/badge.svg" />

# Employee Shift API

The challenge description is available [here](docs/challenge.md).

---

The application explores the [Hexagonal](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) architecture 
leveraging Spring-Boot's dependency injection.

The application makes available a docker image that can be used to deploy on, for example, [Kubernetes](k8s/README.md).

You may find some notes on production readiness [here](docs/production-checklist.md).

# Development

Requirements:
- Java 11

## Building

Docker image:
```sh
$ make build
```

Locally:
```sh
$ make build-local
```

## Running

### Kubernetes

Create if absent the `bphenriques/employee-shifts-api:latest` docker image:
```sh
$ make build
```

Then follow the k8s guide [k8s/README.md](k8s/README.md).

### Docker-Compose

```sh
$ make run
```

#### Locally

```sh
$ make run-local
```

## What is available

The API-Docs are available at http://localhost:8080/swagger-ui.html and there are three additional endpoints on port `8081` which are specific for monitoring:
- **Liveness Probe**: localhost:8081/actuator/health/liveness
- **Readiness Probe**: localhost:8081/actuator/health/readiness
- **Prometheus Metrics**: localhost:8081/actuator/prometheus

**Note**: Kubernetes uses different ports. See the guide [there](k8s/README.md) to see which ports it uses.

## Testing

As it runs in the continuous-integration leveraging local containers using [docker-compose](https://docs.docker.com/compose/):
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
