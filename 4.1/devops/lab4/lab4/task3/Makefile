apply:
	kubectl apply -f web-deployment.yaml
	kubectl apply -f web-service.yaml

get:
	PORT=$(kubectl get svc web -o jsonpath='{.spec.ports[0].nodePort}')
	echo "nodePort = $PORT"
	curl http://localhost:$PORT/

info:
	kubectl get pods -l app=web
	kubectl describe deploy web
	kubectl rollout status deploy/web
	kubectl logs -l app=web --tail=50

nginx-mks:
	minikube service web --url

nginx-pf:
	kubectl port-forward svc/web 8080:80

