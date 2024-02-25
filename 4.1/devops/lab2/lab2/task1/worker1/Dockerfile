FROM golang:1.24-alpine AS builder

WORKDIR /app

COPY main.go .

RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o worker1 main.go

FROM alpine:latest


# Создаем необходимые директории
RUN mkdir -p /var/data /var/result

COPY --from=builder /app/worker1 /usr/local/bin/worker1

RUN chmod +x /usr/local/bin/worker1


WORKDIR /var

CMD ["worker1"]
