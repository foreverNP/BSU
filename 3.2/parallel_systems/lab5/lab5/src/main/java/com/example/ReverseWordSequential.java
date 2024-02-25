package com.example;

import java.io.IOException;

public class ReverseWordSequential {
    public static void main(String[] args) {
        String inputFile = "in.txt";
        String outputFile = "out.txt";
        try {
            long startTime = System.currentTimeMillis();
            java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(inputFile));
            StringBuilder outputBuilder = new StringBuilder();

            for (String line : lines) {
                String[] words = line.split(" ");
                for (String word : words) {
                    String reversed = new StringBuilder(word).reverse().toString();
                    outputBuilder.append(reversed).append(" ");
                }
                outputBuilder.append("\n");
            }

            java.nio.file.Files.write(java.nio.file.Paths.get(outputFile), outputBuilder.toString().getBytes());
            long endTime = System.currentTimeMillis();
            System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
