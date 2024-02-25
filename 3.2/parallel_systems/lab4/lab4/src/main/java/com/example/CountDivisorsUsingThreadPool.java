package com.example;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Scanner;

public class CountDivisorsUsingThreadPool {
    private final static int MAX = 25_000_000;
    private final static int CHUNK_SIZE = 1000;

    private static ConcurrentLinkedQueue<Task> pendingTasks;

    private record Task(int start, int end) {
        public void compute() {
            int highestDivisorCount = 0;
            int bestNumber = 0;
            for (int i = start; i < end; i++) {
                int divCount = calculateDivisorCount(i);
                if (divCount > highestDivisorCount) {
                    highestDivisorCount = divCount;
                    bestNumber = i;
                }
            }
            taskResults.add(new Result(highestDivisorCount, bestNumber));
        }

        private static int calculateDivisorCount(int number) {
            int count = 0;
            int limit = (int) Math.sqrt(number);
            for (int i = 1; i <= limit; i++) {
                if (number % i == 0) {
                    if (i * i == number) {
                        count++;
                    } else {
                        count += 2;
                    }
                }
            }
            return count;
        }
    }

    private static LinkedBlockingQueue<Result> taskResults;

    private record Result(int taskMax, int taskNumber) {
    }

    private static class DivisorCounterThread extends Thread {
        public void run() {
            while (true) {
                Task nextTask = pendingTasks.poll();
                if (nextTask == null) {
                    break;
                }
                nextTask.compute();
            }
        }
    }

    private static void executeDivisorCounting(int threadCount) {
        System.out.println("\nВычисление делителей с использованием " + threadCount + " потоков...");
        long startTime = System.currentTimeMillis();

        taskResults = new LinkedBlockingQueue<>();
        pendingTasks = new ConcurrentLinkedQueue<>();

        DivisorCounterThread[] workers = new DivisorCounterThread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            workers[i] = new DivisorCounterThread();
        }

        int numberOfTasks = (MAX + (CHUNK_SIZE - 1)) / CHUNK_SIZE;
        for (int i = 0; i < numberOfTasks; i++) {
            int taskStart = i * CHUNK_SIZE + 1;
            int taskEnd = (i + 1) * CHUNK_SIZE;
            if (taskEnd > MAX) {
                taskEnd = MAX;
            }

            pendingTasks.add(new Task(taskStart, taskEnd));
        }

        for (int i = 0; i < threadCount; i++)
            workers[i].start();

        int globalMaxCount = 0;
        int globalBestNumber = 0;
        for (int i = 0; i < numberOfTasks; i++) {
            try {
                Result result = taskResults.take();
                if (result.taskMax() > globalMaxCount) {
                    globalMaxCount = result.taskMax();
                    globalBestNumber = result.taskNumber();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;

        System.out.println("\nНаибольшее количество делителей для чисел от 1 до " + MAX + " равно " + globalMaxCount);
        System.out.println("Число с таким количеством делителей: " + globalBestNumber);
        System.out.println("Общее время выполнения: " + (elapsed / 1000.0) + " секунд.\n");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int threadCount = 0;
        while (threadCount < 1 || threadCount > 16) {
            System.out.println("Сколько потоков вы хотите использовать (1 до 16)?");
            threadCount = scanner.nextInt();
            if (threadCount < 1 || threadCount > 16) {
                System.out.println("Пожалуйста, введите число от 1 до 16!");
            }
        }

        executeDivisorCounting(threadCount);
    }

}
