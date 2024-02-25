# RabbitMQ Distributed System

This project implements a distributed RabbitMQ system with Producer and Consumer applications, following the best practices from the referenced articles.

## Architecture

- **RabbitMQ Server**: Message broker with management UI
- **Producer**: Publishes messages to the queue
- **Consumer**: Consumes messages from the queue (2 instances for load balancing)
- **Bind Mounts**: Each application stores data locally for persistence

## Features

- Docker containerization for all components
- Bind mount storage for data persistence
- Health checks and proper service dependencies
- Management utilities via Makefile
- JSON message format with metadata
- Fair message distribution between consumers

## Quick Start

1. **Start the system:**
```bash
make up
```

2. **Check status:**
```bash
make status
```

3. **View logs:**
```bash
make logs
```

4. **Stop the system:**
```bash
make down
```

## Management Commands

### RabbitMQ Control (rabbitmqctl)
```bash
make rabbitmqctl-list-queues      # List all queues
make rabbitmqctl-list-exchanges   # List all exchanges
make rabbitmqctl-list-bindings    # List all bindings
make rabbitmqctl-list-connections # List active connections
make rabbitmqctl-status           # Show RabbitMQ status
```

### RabbitMQ Admin (rabbitmqadmin)
```bash
make rabbitmqadmin-list-queues    # List queues via admin API
make rabbitmqadmin-publish-message # Publish test message
make rabbitmqadmin-get-message    # Get message from queue
```

### Data Inspection
```bash
make inspect-data                 # View stored messages
make monitor                     # Open management UI
```

## Development

### Run applications locally (without Docker)
```bash
# Start RabbitMQ first
make up

# Run producer
make dev-producer

# Run consumer (in another terminal)
make dev-consumer
```

### Customize message count
```bash
# Send 10 messages instead of default 5
docker compose run --rm producer ./main 10
```

## Message Format

```json
{
  "id": 1,
  "content": "Message 1 from producer-1",
  "timestamp": "2023-12-01T10:30:00Z",
  "producer": "producer-1"
}
```
