FROM golang:1.24-alpine AS builder

WORKDIR /app

COPY main.go .

RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o worker2 main.go

FROM alpine:latest

RUN mkdir -p /var/data /var/result

COPY --from=builder /app/worker2 /usr/local/bin/worker2

RUN chmod +x /usr/local/bin/worker2

WORKDIR /var

CMD ["worker2"]
