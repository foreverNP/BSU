# Dice Roller Version 3: Complete Observability Platform

## Overview
A comprehensive Flask application demonstrating full-stack observability with the three pillars of observability: **Traces**, **Metrics**, and **Logs**. This version includes Grafana for unified visualization and correlation of all telemetry data.

## Architecture

```
┌─────────────┐
│  dice-app   │ (Flask + OpenTelemetry)
└──────┬──────┘
       │ OTLP (traces + metrics)
       │ stdout (logs)
       ↓
┌──────────────────┐         ┌──────────────┐
│ otel-collector   │────────→│   Jaeger     │ (Traces)
└──────┬───────────┘         └──────────────┘
       │                              ↑
       │ Prometheus                   │
       │ metrics                      │
       ↓                              │
┌──────────────┐               ┌──────┴───────┐
│  Prometheus  │←──────────────│   Grafana    │ (Unified UI)
└──────────────┘               └──────┬───────┘
                                      │
┌──────────────┐          ┌──────────┴┘
│   Promtail   │─────────→│    Loki      │ (Logs)
└──────────────┘          └──────────────┘
       ↑
       │ (scrapes container logs)
       │
┌──────┴──────┐
│  dice-app   │
│  container  │
└─────────────┘
```

## Components

| Service | Purpose | Port |
|---------|---------|------|
| **dice-app** | Flask application with instrumentation | 8080 |
| **otel-collector** | Centralized telemetry processor | 4317, 8889 |
| **Jaeger** | Distributed tracing backend | 16686 |
| **Prometheus** | Metrics storage and querying | 9090 |
| **Loki** | Log aggregation system | 3100 |
| **Promtail** | Log collector agent | - |
| **Grafana** | Unified observability dashboard | 3000 |

## Features

✅ **Step 9**: Grafana deployment with auto-provisioned datasources  
✅ **Step 10**: Structured logging with correlation  
✅ **Step 11**: Loki + Promtail for log aggregation  
✅ **Step 12**: Unified telemetry exploration in Grafana  

### Observability Features

- 🔍 **Distributed Tracing**: Track requests across services
- 📊 **Custom Metrics**: Counter for dice roll distribution
- 📝 **Structured Logs**: Centralized log collection and search
- 🔗 **Data Correlation**: Link traces, metrics, and logs
- 📈 **Pre-built Dashboard**: Ready-to-use visualization
- 🎯 **Real-time Monitoring**: Live data streaming

## Project Structure

```
├── app.py                          # Flask app with OTel instrumentation
├── requirements.txt                # Python dependencies
├── Dockerfile                      # Application container
├── docker-compose.yml              # Service orchestration
├── otel-collector-config.yaml      # Telemetry processing config
├── prometheus.yml                  # Metrics scraping config
├── loki-config.yaml               # Log storage config
├── promtail-config.yaml           # Log collection config
└── grafana/
    └── provisioning/
        ├── datasources/
        │   └── datasources.yaml   # Auto-configure datasources
        └── dashboards/
            ├── dashboard.yaml      # Dashboard provider
            └── dice-dashboard.json # Pre-built dashboard
```

## Installation & Setup

### 1. Start All Services

```bash
docker-compose up -d
```

## Usage

### Generate Traffic

```bash
# Single roll
curl "http://localhost:8080/rolldice?player=alice"

# Generate 100 rolls for testing
for i in {1..100}; do
  curl "http://localhost:8080/rolldice?player=player$((i % 5))"
  sleep 0.5
done

# Stress test (fast rolls)
for i in {1..1000}; do
  curl -s "http://localhost:8080/rolldice?player=player$((RANDOM % 10))" > /dev/null
done
```

### Health Check

```bash
curl http://localhost:8080/health
```

## Step 12: Exploring Telemetry in Grafana

### Access Grafana

1. Open browser: **http://localhost:3000**
2. Login credentials:
   - Username: `admin`
   - Password: `admin`
3. Skip password change (or set new password)

### Step 10: Working with Logs

#### Explore Logs in Grafana

1. Click **Explore** (compass icon in sidebar)
2. Select **Loki** from datasource dropdown
3. Use the log browser or write LogQL queries

#### Log Features

- **Time Range Selection**: Adjust time window
- **Live Tail**: Stream logs in real-time
- **Log Details**: Click any log line to see full details
- **Context**: View surrounding logs
- **Labels**: Filter by container, stream, level

### Explore Metrics

1. Click **Explore**
2. Select **Prometheus** datasource
3. Use the metrics browser or write PromQL

### Explore Traces

1. Click **Explore**
2. Select **Jaeger** datasource
3. Configure query:
   - Service: `dice-server`
   - Operation: All or specific
   - Lookback: Last 1 hour

**Trace Details:**
- Request duration
- Span hierarchy (HTTP request → roll span)
- Custom attributes: `roll.value`
- Error status and logs
- Service graph

## Configuration Details

### OTLP Collector Pipeline

**Traces Pipeline:**
```
OTLP Receiver → Batch Processor → Resource Processor → [Jaeger, Debug]
```

**Metrics Pipeline:**
```
OTLP Receiver → Batch Processor → [Prometheus, Debug]
```

### Loki Retention

Default: 744h (31 days)
- Stored in: `/loki` volume
- Schema: v13 (TSDB)
- Compression: Enabled

### Prometheus Scrape

- Interval: 15s
- Evaluation: 15s
- Retention: Default (15 days)

### Grafana Datasources

All datasources are auto-provisioned:
- **Prometheus**: Default datasource
- **Loki**: Logs datasource with trace correlation
- **Jaeger**: Traces datasource with log correlation

## Metrics Reference

| Metric | Type | Description |
|--------|------|-------------|
| `dice_app_dice_rolls_total` | Counter | Total dice rolls by value |
| `http_server_duration_milliseconds` | Histogram | Request duration |
| `otelcol_receiver_accepted_spans` | Counter | Spans received |
| `otelcol_exporter_sent_spans` | Counter | Spans exported |

## Log Labels

| Label | Example | Description |
|-------|---------|-------------|
| `service` | dice-app | Service name |
| `container` | dice-app | Container name |
| `stream` | stdout | Log stream |
| `level` | INFO | Log level |
| `logger` | app | Logger name |

## Trace Attributes

| Attribute | Example | Description |
|-----------|---------|-------------|
| `service.name` | dice-server | Service identifier |
| `roll.value` | 6 | Dice roll result |
| `http.method` | GET | HTTP method |
| `http.route` | /rolldice | Route pattern |
| `http.status_code` | 200 | Response status |
