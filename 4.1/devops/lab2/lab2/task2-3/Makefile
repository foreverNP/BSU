.PHONY: help build up down logs status clean producer consumer rabbitmqctl rabbitmqadmin

help:
	@echo "RabbitMQ Distributed System Management"
	@echo "======================================"
	@echo ""
	@echo "Available commands:"
	@echo "  build        - Build all Docker images"
	@echo "  up           - Start the entire system"
	@echo "  down         - Stop the entire system"
	@echo "  logs         - Show logs from all services"
	@echo "  status       - Show system status"
	@echo "  clean        - Clean up containers and volumes"
	@echo "  producer     - Run producer manually"
	@echo "  consumer     - Run consumer manually"
	@echo "  rabbitmqctl  - Execute rabbitmqctl commands"
	@echo "  rabbitmqadmin- Execute rabbitmqadmin commands"
	@echo ""

build:
	@echo "Building all Docker images..."
	docker compose build

up:
	@echo "Starting RabbitMQ distributed system..."
	docker compose up -d
	@echo "System started! Management UI available at http://localhost:15672"
	@echo "Username: admin, Password: admin"

down:
	@echo "Stopping RabbitMQ distributed system..."
	docker compose down

logs:
	docker compose logs -f

status:
	@echo "=== System Status ==="
	docker compose ps
	@echo ""
	@echo "=== RabbitMQ Queues ==="
	docker compose exec rabbitmq rabbitmqctl list_queues name messages consumers
	@echo ""
	@echo "=== RabbitMQ Exchanges ==="
	docker compose exec rabbitmq rabbitmqctl list_exchanges name type
	@echo ""
	@echo "=== RabbitMQ Bindings ==="
	docker compose exec rabbitmq rabbitmqctl list_bindings

clean:
	@echo "Cleaning up containers and volumes..."
	docker compose down -v
	docker system prune -f

producer:
	@echo "Running producer..."
	docker compose run --rm producer

consumer:
	@echo "Running consumer..."
	docker compose run --rm consumer-1

rabbitmqctl:
	@echo "Available rabbitmqctl commands:"
	@echo "  make rabbitmqctl-list-queues"
	@echo "  make rabbitmqctl-list-exchanges"
	@echo "  make rabbitmqctl-list-bindings"
	@echo "  make rabbitmqctl-list-connections"
	@echo "  make rabbitmqctl-list-channels"
	@echo "  make rabbitmqctl-status"

rabbitmqctl-list-queues:
	docker compose exec rabbitmq rabbitmqctl list_queues name messages consumers

rabbitmqctl-list-exchanges:
	docker compose exec rabbitmq rabbitmqctl list_exchanges name type

rabbitmqctl-list-bindings:
	docker compose exec rabbitmq rabbitmqctl list_bindings

rabbitmqctl-list-connections:
	docker compose exec rabbitmq rabbitmqctl list_connections

rabbitmqctl-list-channels:
	docker compose exec rabbitmq rabbitmqctl list_channels

rabbitmqctl-status:
	docker compose exec rabbitmq rabbitmqctl status

rabbitmqadmin:
	@echo "Available rabbitmqadmin commands:"
	@echo "  make rabbitmqadmin-list-queues"
	@echo "  make rabbitmqadmin-list-exchanges"
	@echo "  make rabbitmqadmin-publish-message"
	@echo "  make rabbitmqadmin-get-message"

rabbitmqadmin-list-queues:
	docker compose exec rabbitmq rabbitmqadmin list queues

rabbitmqadmin-list-exchanges:
	docker compose exec rabbitmq rabbitmqadmin list exchanges

rabbitmqadmin-publish-message:
	@echo "Publishing test message..."
	docker compose exec rabbitmq rabbitmqadmin publish exchange= routing_key=task_queue payload="Test message from rabbitmqadmin"

rabbitmqadmin-get-message:
	@echo "Getting message from queue..."
	docker compose exec rabbitmq rabbitmqadmin get queue=task_queue

dev-producer:
	@echo "Running producer in development mode..."
	cd producer && go run main.go 3

dev-consumer:
	@echo "Running consumer in development mode..."
	cd consumer && go run main.go

monitor:
	@echo "Opening RabbitMQ Management UI..."
	@echo "URL: http://localhost:15672"
	@echo "Username: admin"
	@echo "Password: admin"
	@if command -v start >/dev/null 2>&1; then start http://localhost:15672; fi
	@if command -v open >/dev/null 2>&1; then open http://localhost:15672; fi

inspect-data:
	@echo "=== Producer Messages ==="
	@if [ -f data/producer_messages.log ]; then cat data/producer_messages.log; else echo "No producer messages found"; fi
	@echo ""
	@echo "=== Consumer Messages ==="
	@if [ -f data/consumer_messages.log ]; then cat data/consumer_messages.log; else echo "No consumer messages found"; fi
