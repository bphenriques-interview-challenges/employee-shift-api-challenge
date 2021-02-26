ROOT_DIR=$(shell pwd)

#
# Main Targets
#
.PHONY: test
test:
	sh $(ROOT_DIR)/ci/test.sh

.PHONY: run-dockerized
run-dockerized:
	docker-compose up --build run

#
# Local development
#
.PHONY: run
run:
	docker-compose up -d postgres
	echo "Waiting for dependencies to start..."
	sleep 5
	bash -c "trap 'docker-compose down' EXIT 1; env $(shell egrep -v '^#' $(ROOT_DIR)/environment.local.env | xargs) $(ROOT_DIR)/gradlew web-app:bootRun"

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
