ROOT_DIR=$(shell pwd)

.PHONY: run
run:
	docker-compose up -d postgres
	echo "Waiting for dependencies to start..."
	sleep 10 # Replace with proper pooling
	bash -c "trap 'docker-compose down' EXIT 1; env $(shell egrep -v '^#' $(ROOT_DIR)/local.env | xargs) $(ROOT_DIR)/gradlew bootRun"

.PHONY: test
test:
	sh $(ROOT_DIR)/ci/test.sh

.PHONY: test-dependencies-up
test-dependencies-up:
	docker-compose up postgres
	# You may now run the tests locally through IntelliJ or through command-line
	#
	# Ctrl-C when finished.

.PHONY: lint
lint:
	$(ROOT_DIR)/gradlew ktlintCheck

.PHONY: format
format:
	$(ROOT_DIR)/gradlew ktlintFormat

#
# Docker Targets
#
.PHONY: build-docker-image
build-docker-image:
	docker build . --tag bphenriques/employee-shifts-api

.PHONY: run-dockerized
run-dockerized:
	docker-compose up --build employee-shifts-api
