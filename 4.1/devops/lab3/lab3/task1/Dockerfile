FROM python:3.11-slim

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends build-essential && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0
ENV FLASK_RUN_PORT=8080

RUN opentelemetry-bootstrap -a install || true

ENTRYPOINT ["opentelemetry-instrument", "flask", "run", "--host", "0.0.0.0", "-p", "8080", "--no-reload"]