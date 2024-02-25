# Dice Roller with OpenTelemetry

## Version 2: Full Observability Stack

### Overview
An enhanced Flask application demonstrating full observability with OpenTelemetry: distributed tracing, metrics collection, and centralized telemetry processing through an OpenTelemetry Collector.

### Architecture
```
dice-app → otel-collector → Jaeger (traces)
                          → Prometheus (metrics)
```

### Components
- **dice-app**: Flask application with traces and metrics
- **otel-collector**: Central telemetry processing hub
- **Jaeger**: Distributed tracing backend
- **Prometheus**: Metrics storage and querying

### Prerequisites
- Docker
- Docker Compose

### Project Structure
```
.
├── app.py                      # Flask application with metrics
├── requirements.txt            # Python dependencies
├── Dockerfile                  # Container definition
├── docker-compose.yml          # Service orchestration
├── otel-collector-config.yaml  # Collector configuration
└── prometheus.yml              # Prometheus scrape config
```

### Installation & Setup

1. **Create all project files** (see configuration files below)

2. **Start all services**:
```bash
docker-compose up -d
```

3. **Verify all services are healthy**:
```bash
docker-compose ps
```

### Usage

#### Roll the dice
```bash
# Generate some data
for i in {1..20}; do
  curl "http://localhost:8080/rolldice?player=player$((i % 3))"
done
```

#### View Traces (Jaeger)
Navigate to: `http://localhost:16686`

1. Select service: `dice-server`
2. Click "Find Traces"
3. Explore trace details:
   - HTTP request span
   - Custom "roll" span with `roll.value` attribute

#### View Metrics (Prometheus)
Navigate to: `http://localhost:9090`

**Query examples**:
```promql
# Total dice rolls
dice_app_dice_rolls_total

# Rolls by value
dice_app_dice_rolls_total{roll_value="6"}

# Rate of rolls per second
rate(dice_app_dice_rolls_total[1m])

# Total rolls grouped by value
sum by (roll_value) (dice_app_dice_rolls_total)
```

**Create a graph**:
1. Go to "Graph" tab
2. Enter query: `dice_app_dice_rolls_total`
3. Click "Execute"
4. Switch to "Graph" view

### Features
- ✅ Distributed tracing with Jaeger
- ✅ Custom metrics with OpenTelemetry Metrics API
- ✅ Centralized telemetry processing (OTLP Collector)
- ✅ Prometheus metrics scraping
- ✅ Refactored code with separated business logic
- ✅ Correlation between traces and metrics

### Configuration Files

#### otel-collector-config.yaml
Configures how the collector receives, processes, and exports telemetry data.

**Key sections**:
- **Receivers**: Accept OTLP data on ports 4317 (gRPC) and 4318 (HTTP)
- **Processors**: Batch telemetry for efficient export
- **Exporters**: 
  - Send traces to Jaeger
  - Expose metrics for Prometheus scraping
- **Pipelines**: Define data flow for traces and metrics

#### prometheus.yml
Configures Prometheus to scrape metrics from the collector.

**Scrape targets**:
- `otel-collector:8889` - Application metrics
- `otel-collector:8888` - Collector's own metrics

### Environment Variables

| Variable | Value | Description |
|----------|-------|-------------|
| `OTEL_TRACES_EXPORTER` | `otlp` | Export traces via OTLP |
| `OTEL_METRICS_EXPORTER` | `otlp` | Export metrics via OTLP |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://otel-collector:4317` | Collector endpoint |
| `OTEL_SERVICE_NAME` | `dice-server` | Service name |

### Ports

| Service | Port | Purpose |
|---------|------|---------|
| dice-app | 8080 | HTTP API |
| jaeger | 16686 | Jaeger UI |
| prometheus | 9090 | Prometheus UI |
| otel-collector | 4317 | OTLP gRPC |
| otel-collector | 4318 | OTLP HTTP |
| otel-collector | 8889 | Metrics for Prometheus |
