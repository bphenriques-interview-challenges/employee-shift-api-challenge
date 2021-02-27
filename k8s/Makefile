.PHONY: deploy-postgres
deploy-postgres:
	kubectl create -f postgres-configmap.yaml
	kubectl create -f postgres-storage.yaml
	kubectl create -f postgres-deployment.yaml
	kubectl create -f postgres-service.yaml

check-postgres:
	kubectl get svc postgres

delete-postgres:
	kubectl delete service postgres
	kubectl delete deployment postgres
	kubectl delete configmap postgres-config
	kubectl delete persistentvolumeclaim postgres-pv-claim
	kubectl delete persistentvolume postgres-pv-volume
