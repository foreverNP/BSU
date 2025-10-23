# Как запускать  

1. Перейдите в папку `terraform`:

```bash
cd terraform
terraform init
terraform apply -auto-approve
```

2. После завершения terraform, проверьте ноды:

```bash
kubectl get nodes -o wide
```

Ожидаемый вывод (пример для kind, 3 ноды):

```
NAME              STATUS   ROLES           AGE   VERSION
tf-kind-cluster-control-plane   Ready    control-plane   1m    v1.xx.x
tf-kind-cluster-worker          Ready    <none>          1m    v1.xx.x
tf-kind-cluster-worker2         Ready    <none>          1m    v1.xx.x
```

3. Примените Ansible playbook:

```bash
cd ansible
ansible-playbook -i inventory.ini deploy-nginx.yml
```

4. Проверка результата (команды):

```bash
kubectl get daemonset -n default
kubectl get pods -o wide -n default
kubectl get pods -o wide -n default --selector=app=nginx
```

5. (Опционально) Удаление кластера:

* для minikube:

```bash
minikube delete
```

```bash
terraform destroy -auto-approve
```