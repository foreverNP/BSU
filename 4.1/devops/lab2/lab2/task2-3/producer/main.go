package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"os"
	"strconv"
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

	// Get message count from command line or use default
	messageCount := 10
	if len(os.Args) > 1 {
		if count, err := strconv.Atoi(os.Args[1]); err == nil {
			messageCount = count
		}
	}

	producerID := os.Getenv("PRODUCER_ID")
	if producerID == "" {
		producerID = "producer-1"
	}

	log.Printf("Producer %s starting to send %d messages", producerID, messageCount)

	for i := 1; i <= messageCount; i++ {
		// Create message
		message := Message{
			ID:        i,
			Content:   fmt.Sprintf("Message %d from %s", i, producerID),
			Timestamp: time.Now(),
			Producer:  producerID,
		}

		// Serialize message
		body, err := json.Marshal(message)
		failOnError(err, "Failed to marshal message")

		// Publish message
		ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
		err = ch.PublishWithContext(ctx,
			"",     // exchange
			q.Name, // routing key
			false,  // mandatory
			false,  // immediate
			amqp.Publishing{
				DeliveryMode: amqp.Persistent,
				ContentType:  "application/json",
				Body:         body,
			})
		cancel()
		failOnError(err, "Failed to publish a message")

		// Write to local file (bind mount storage)
		err = writeToFile("/data/producer_messages.log", message)
		if err != nil {
			log.Printf("Warning: Failed to write to file: %v", err)
		}

		log.Printf(" [x] Sent message %d: %s", i, message.Content)
		time.Sleep(1 * time.Second) // Rate limiting
	}

	log.Printf("Producer %s finished sending messages", producerID)
}
