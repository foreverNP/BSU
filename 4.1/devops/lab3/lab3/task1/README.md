# Dice Roller with OpenTelemetry

## Version 1: Direct Jaeger Integration

### Overview
A simple Flask application that demonstrates distributed tracing using OpenTelemetry with direct Jaeger export. The app simulates a dice rolling game and exports traces directly to Jaeger.

### Architecture
```
dice-app → Jaeger (via OTLP gRPC)
```

### Components
- **dice-app**: Flask application with OpenTelemetry auto-instrumentation
- **Jaeger**: All-in-one tracing backend with UI

### Prerequisites
- Docker
- Docker Compose

### Project Structure
```
.
├── app.py              # Flask application
├── requirements.txt    # Python dependencies
├── Dockerfile         # Container definition
└── docker-compose.yml # Service orchestration
```

### Installation & Setup

1. **Clone or create the project files**

2. **Start the services**:
```bash
docker-compose up -d
```

3. **Verify services are running**:
```bash
docker-compose ps
```

### Usage

#### Roll the dice
```bash
# Anonymous player
curl http://localhost:8080/rolldice

# Named player
curl "http://localhost:8080/rolldice?player=iplayer"
```

#### View traces
Open your browser and navigate to:
```
http://localhost:16686
```
- Select service: `dice-server`
- Click "Find Traces"
- Explore individual traces and spans

### Features
- ✅ Automatic Flask instrumentation
- ✅ Manual span creation with custom attributes
- ✅ Log correlation with traces
- ✅ OTLP gRPC export to Jaeger

### Environment Variables

| Variable | Value | Description |
|----------|-------|-------------|
| `OTEL_TRACES_EXPORTER` | `otlp` | Export traces via OTLP |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://jaeger:4317` | Jaeger OTLP endpoint |
| `OTEL_SERVICE_NAME` | `dice-server` | Service identifier |
| `OTEL_METRICS_EXPORTER` | `none` | Disable metrics |
| `OTEL_LOGS_EXPORTER` | `none` | Disable logs export |

### Ports

| Service | Port | Purpose |
|---------|------|---------|
| dice-app | 8080 | HTTP API |
| jaeger | 16686 | Jaeger UI |
| jaeger | 4317 | OTLP gRPC receiver |
