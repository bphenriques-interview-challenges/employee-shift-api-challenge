# Employee Shift API

The challenge description is available [here](docs/challenge.md).

# Development

Requirements:
- Java 11

The project leverages [GNU Make](https://www.gnu.org/software/make/) to make workflows easier. Check the `Makefile` to see
the underlying commands it runs.

## Running

Locally:
```sh
$ make run
```

Dockerized:
```
$ make run-dockerized
```

## Testing

Full tests:
```sh
$ make test
```

Launch tests dependencies in order to run the tests manually (for speed at the expense of slight inaccuracy):
```sh
$ make test-dependencies-up
```

You may now run the `test` gradle target.

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
* `infrastructure`: Interaction with the Database.
* `web-app`: Exposes HTTP interface.
* `common-test`: As the name says.  
* `db`: Data migrations scripts.
* `docs`: Relevant documentation.
* `ci`: Continuous integrations scripts.
* `buildSrc` ajd `gradle`: Relevant folders to build the project itself.
