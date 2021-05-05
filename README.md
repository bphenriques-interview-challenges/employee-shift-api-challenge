<p align="center">
  <img src="https://github.com/bphenriques-lab/employee-shift-api-challenge/actions/workflows/build.yaml/badge.svg" />
  <img alt="Kotlin" src="https://img.shields.io/static/v1?style=flat-square&logo=Kotlin&label=&message=Kotlin&color=gray">
  <img alt="Spring Boot" src="https://img.shields.io/static/v1?style=flat-square&logo=Spring&label=&message=Spring%20Boot&color=gray">
  <img alt="PostgreSQL" src="https://img.shields.io/static/v1?style=flat-square&logo=PostgreSQL&label=&message=PostgreSQL&color=gray">
  <img alt="Docker" src="https://img.shields.io/static/v1?style=flat-square&logo=Docker&label=&message=Docker&color=gray">
  <img alt="Kubernetes" src="https://img.shields.io/static/v1?style=flat-square&logo=Kubernetes&label=&message=Kubernetes&color=gray">
</p>

# Employee Shift API

The project's description is available in [docs/requirements.md](docs/requirements.md). This was made as part of a challenge in early 2021. Let
me know how you would approach the project!

---

The application explores:
- [Hexagonal](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) architecture leveraging Spring-Boot's dependency injection
- [Spring Data](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/).
- [Spring Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html).
- [Swagger + SprintBoot](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api).
- Gradle Kotlin DSL, `buildSrc` and submodules.
- Containerization either through [docker-compose](https://docs.docker.com/compose/) or through [Kubernetes](k8s/README.md).

You will find my notes on the project under [docs/project-notes.md](docs/project-notes.md), and notes regarding production readiness under [docs/production-checklist.md](docs/production-checklist.md).

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
