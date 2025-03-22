package com.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ReverseWordBlockingQueue {
    private static final int QUEUE_CAPACITY = 1000;
    private static volatile boolean producerFinished = false;

    public static void main(String[] args) {
        String inputFile = "in.txt";
        String outputFile = "out.txt";

        BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

        // Поток-производитель
        Thread producer = new Thread(() -> {
            try {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(inputFile));
                for (String line : lines) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        queue.put(word);
                    }
                    queue.put("\n");
                }
                producerFinished = true;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Поток-потребитель
        Thread consumer = new Thread(() -> {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {
                while (true) {
                    String word = queue.poll();
                    if (word != null) {
                        if (word.equals("\n")) {
                            writer.newLine();
                        } else {
                            String reversed = new StringBuilder(word).reverse().toString();
                            writer.write(reversed + " ");
                        }
                    } else if (producerFinished) {
                        break;
                    }
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        long startTime = System.currentTimeMillis();

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
            long endTime = System.currentTimeMillis();
            System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
