package com.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.Scanner;

public class CountDivisorsUsingExecutor {
    private final static int MAX = 1_000_000;
    private final static int TASK_SIZE = 1000;

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

    private static void executeDivisorCounting(int threadCount) {
        System.out.println("\nПодсчет делителей с использованием " + threadCount + " потоков...");

        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        ArrayList<Future<DivisorResult>> futureResults = new ArrayList<>();

        int taskCount = (MAX + TASK_SIZE - 1) / TASK_SIZE;
        for (int i = 0; i < taskCount; i++) {
            int start = i * TASK_SIZE + 1;
            int end = Math.min((i + 1) * TASK_SIZE, MAX);
            Future<DivisorResult> taskFuture = executor.submit(new DivisorTask(start, end));
            futureResults.add(taskFuture);
        }

        int maxDivisorCount = 0;
        int intWithMaxDivisors = 0;
        for (Future<DivisorResult> taskFuture : futureResults) {
            try {
                DivisorResult result = taskFuture.get();
                if (result.maxDivisorCount > maxDivisorCount) {
                    maxDivisorCount = result.maxDivisorCount;
                    intWithMaxDivisors = result.numberWithMaxDivisors;
                }
            } catch (Exception e) {
                System.out.println("Ошибка при обработке задачи: " + e.getMessage());
                executor.shutdownNow();
                return;
            }
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("\nНаибольшее количество делителей " + "для чисел от 1 до " + MAX + " равно "
                + maxDivisorCount);
        System.out.println("Число с таким количеством делителей: " + intWithMaxDivisors);
        System.out.println("Общее затраченное время: " + (elapsedTime / 1000.0) + " секунд.\n");

        executor.shutdown();
    }

    private record DivisorResult(int maxDivisorCount, int numberWithMaxDivisors) {
    }

    private record DivisorTask(int min, int max) implements Callable<DivisorResult> {
        public DivisorResult call() {
            int maxDivisors = 0;
            int numberWithMax = 0;
            for (int i = min; i <= max; i++) {
                int divisors = countDivisors(i);
                if (divisors > maxDivisors) {
                    maxDivisors = divisors;
                    numberWithMax = i;
                }
            }
            return new DivisorResult(maxDivisors, numberWithMax);
        }

        private int countDivisors(int num) {
            int count = 0;
            int limit = (int) Math.sqrt(num);
            for (int i = 1; i <= limit; i++) {
                if (num % i == 0) {
                    count++;
                    if (i * i != num) {
                        count++;
                    }
                }
            }
            return count;
        }
    }
}
