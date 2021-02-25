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

## Testing

Testing:
```sh
$ make test
```

If you wish to run the tests locally (for speed at the expense of slight inaccuracy):
```sh
$ make test-dependencies-up
```

## Nail polishing

Linter:
```sh
$ make lint
```

Format:
```sh
$ make format
```

## Project Structure

* `db`: Data migrations scripts.
* `docs`: Relevant documentation.
* `ci`: Continuous integrations scripts.
* `src` ajd `gradle`: Relevant folders to build the project itself.
