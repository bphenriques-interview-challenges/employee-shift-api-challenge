ROOT_DIR=$(shell pwd)

.PHONY: deploy-postgres
deploy-postgres:
	kubectl create -f $(ROOT_DIR)/postgres/configmap.yaml
	kubectl create -f $(ROOT_DIR)/postgres/storage.yaml
	kubectl create -f $(ROOT_DIR)/postgres/deployment.yaml
	kubectl create -f $(ROOT_DIR)/postgres/service.yaml

check-postgres:
	kubectl get svc postgres
	kubectl get svc postgres -o jsonpath='{.spec.ports[0].nodePort}'

delete-postgres:
	kubectl delete service postgres
	kubectl delete deployment postgres
	kubectl delete configmap postgres-config
	kubectl delete persistentvolumeclaim postgres-pv-claim
	kubectl delete persistentvolume postgres-pv-volume
