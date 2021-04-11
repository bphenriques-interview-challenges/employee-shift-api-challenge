<img src="https://github.com/bphenriques-lab/employee-shift-api-challenge/actions/workflows/build.yaml/badge.svg" />

# Employee Shift API

The project's description is available [here](docs/project.md).

---

The application explores:
- [Hexagonal](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) architecture leveraging Spring-Boot's dependency injection
- [Spring Data](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/).
- [Spring Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html).
- [Swagger + SprintBoot](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api).
- Containerization either through [docker-compose](https://docs.docker.com/compose/) or through [Kubernetes](k8s/README.md).

I left some notes regarding production readiness at [docs/production-checklist.md](docs/production-checklist.md).

**Note:** This project was made as part of a challenge. What would you do differently?

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

Create if absent the docker images:
```sh
$ make build
```

Then follow the k8s guide at [k8s/README.md](k8s/README.md).

### Docker-Compose

```sh
$ make run
```

### Locally

```sh
$ make run-local
```

## What is available

Application:
- **API-Docs**: http://localhost:8080/swagger-ui.html.

Management:
- **Liveness Probe**: http://localhost:8081/actuator/health/liveness
- **Readiness Probe**: http://localhost:8081/actuator/health/readiness
- **Prometheus Metrics**: http://localhost:8081/actuator/prometheus

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
* `k8`: Folder demonstrating how to deploy the application on K8S.
