apply:
	kubectl apply -f volume-claim.yaml
	kubectl get pvc          
	kubectl apply -f pod-with-vol.yaml
	kubectl get pods


# kubectl exec -it pod/web-pod -- /bin/sh
# cd /data
# echo "Hello world!" > sample.txt
# exit

# kubectl delete pod/web-pod
# kubectl apply -f pod-with-vol.yaml
# kubectl exec -it pod/web-pod -- /bin/sh -c "cat /data/sample.txt"
