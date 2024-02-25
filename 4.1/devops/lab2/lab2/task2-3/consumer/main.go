package main

import (
	"encoding/json"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	amqp "github.com/rabbitmq/amqp091-go"
)

type Message struct {
	ID        int       `json:"id"`
	Content   string    `json:"content"`
	Timestamp time.Time `json:"timestamp"`
	Producer  string    `json:"producer"`
}

func failOnError(err error, msg string) {
	if err != nil {
		log.Panicf("%s: %s", msg, err)
	}
}

func writeToFile(filename string, message Message) error {
	file, err := os.OpenFile(filename, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0o644)
	if err != nil {
		return err
	}
	defer file.Close()

	messageJSON, err := json.Marshal(message)
	if err != nil {
		return err
	}

	_, err = file.WriteString(string(messageJSON) + "\n")
	return err
}

func main() {
	// RabbitMQ connection
	conn, err := amqp.Dial("amqp://admin:admin@rabbitmq:5672/")
	failOnError(err, "Failed to connect to RabbitMQ")
	defer conn.Close()

	ch, err := conn.Channel()
	failOnError(err, "Failed to open a channel")
	defer ch.Close()

	// Declare queue
	q, err := ch.QueueDeclare(
		"task_queue", // name
		true,         // durable
		false,        // delete when unused
		false,        // exclusive
		false,        // no-wait
		nil,          // arguments
	)
	failOnError(err, "Failed to declare a queue")

	// Set QoS for fair dispatch
	err = ch.Qos(
		1,     // prefetch count
		0,     // prefetch size
		false, // global
	)
	failOnError(err, "Failed to set QoS")

	// Register consumer
	msgs, err := ch.Consume(
		q.Name, // queue
		"",     // consumer
		false,  // auto-ack
		false,  // exclusive
		false,  // no-local
		false,  // no-wait
		nil,    // args
	)
	failOnError(err, "Failed to register a consumer")

	consumerID := os.Getenv("CONSUMER_ID")
	if consumerID == "" {
		consumerID = "consumer-1"
	}

	log.Printf("Consumer %s started, waiting for messages...", consumerID)

	// Handle graceful shutdown
	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)

	go func() {
		for d := range msgs {
			var message Message
			err := json.Unmarshal(d.Body, &message)
			if err != nil {
				log.Printf("Error unmarshaling message: %v", err)
				d.Nack(false, false) // Reject message
				continue
			}

			log.Printf("Consumer %s received message %d: %s (from %s)",
				consumerID, message.ID, message.Content, message.Producer)

			// Simulate processing time
			time.Sleep(2 * time.Second)

			// Write to local file (bind mount storage)
			err = writeToFile("/data/consumer_messages.log", message)
			if err != nil {
				log.Printf("Warning: Failed to write to file: %v", err)
			}

			log.Printf("Consumer %s processed message %d", consumerID, message.ID)

			// Acknowledge message
			d.Ack(false)
		}
	}()

	// Wait for shutdown signal
	<-sigChan
	log.Printf("Consumer %s shutting down...", consumerID)
}
