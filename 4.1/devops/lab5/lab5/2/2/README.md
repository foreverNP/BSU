
## Как запускать
1. Пример — site deployment (демонстрационный, использует minikube ssh для выполнения команд на нодах):
   ```bash
   ansible-playbook -c local playbooks/site.yml
   ```
2. Демонстрация rolling update (последовательное обновление нод):
   ```bash
   ansible-playbook -c local playbooks/rolling_update.yml
   ```
