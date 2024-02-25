FROM rabbitmq:3.10.7-management-alpine

ENV RABBITMQ_DEFAULT_USER=admin
ENV RABBITMQ_DEFAULT_PASS=admin
ENV RABBITMQ_DEFAULT_VHOST=/

RUN mkdir -p /var/lib/rabbitmq/mnesia

EXPOSE 5672 15672

CMD ["rabbitmq-server"]
