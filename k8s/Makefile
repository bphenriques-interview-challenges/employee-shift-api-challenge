ROOT_DIR=$(shell pwd)

.PHONY: deploy
deploy: deploy-postgres deploy-employee-shifts-api status

# Flimsy if deploy went wrong. Run commands individually to clean up.
delete: delete-employee-shifts-api delete-postgres

# Useful: https://kubernetes.io/docs/reference/kubectl/cheatsheet/
.PHONY: status
status:
	# Check deployed applications
	kubectl get deployments

	# List pods
	kubectl get pods

	# Check Port with
	kubectl get svc postgres employee-shifts-api-entrypoint

#
# Auxiliary Targets
#
.PHONY: deploy-employee-shifts-api
deploy-employee-shifts-api:
	kubectl create -f $(ROOT_DIR)/employee-shifts-api/configmap.yaml
	kubectl create -f $(ROOT_DIR)/employee-shifts-api/deployment.yaml
	kubectl create -f $(ROOT_DIR)/employee-shifts-api/service.yaml

.PHONY: delete-employee-shifts-api
delete-employee-shifts-api:
	kubectl delete service employee-shifts-api-entrypoint
	kubectl delete deployment employee-shifts-api
	kubectl delete configmap employee-shifts-api-config

.PHONY: deploy-postgres
deploy-postgres:
	kubectl create -f $(ROOT_DIR)/postgres/configmap.yaml
	kubectl create -f $(ROOT_DIR)/postgres/storage.yaml
	kubectl create -f $(ROOT_DIR)/postgres/deployment.yaml
	kubectl create -f $(ROOT_DIR)/postgres/service.yaml

# It also deletes the persistent volume. In a real scenario it shouldn't happen.
.PHONY: delete-postgres
delete-postgres:
	kubectl delete service postgres
	kubectl delete deployment postgres
	kubectl delete configmap postgres-config
	kubectl delete persistentvolumeclaim postgres-pv-claim
	kubectl delete persistentvolume postgres-pv-volume
