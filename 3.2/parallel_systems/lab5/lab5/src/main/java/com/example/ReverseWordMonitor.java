package com.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class ReverseWordMonitor {
    private static final String END_MARKER = "###END###";

    public static void main(String[] args) {
        String inputFile = "in.txt";
        String outputFile = "out.txt";
        WordQueue queue = new WordQueue(1000);

        // Поток-производитель
        Thread producer = new Thread(() -> {
            try {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(inputFile));
                for (String line : lines) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        if (!word.isEmpty()) {
                            queue.put(word);
                        }
                    }
                    queue.put("\n");
                }
                queue.put(END_MARKER);
            } catch (IOException e) {
                e.printStackTrace();
                queue.put(END_MARKER);
            }
        });

        // Поток-потребитель
        Thread consumer = new Thread(() -> {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {
                while (true) {
                    String word = queue.take();

                    if (END_MARKER.equals(word)) {
                        break;
                    }

                    if (word.equals("\n")) {
                        writer.newLine();
                    } else {
                        String reversed = new StringBuilder(word).reverse().toString();
                        writer.write(reversed + " ");
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

class WordQueue {
    private final LinkedList<String> queue = new LinkedList<>();
    private final int capacity;

    public WordQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(String word) {
        while (queue.size() == capacity) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        queue.add(word);
        notify();
    }

    public synchronized String take() {
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        String word = queue.remove();
        notify();
        return word;
    }
}
