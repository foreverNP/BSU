package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

public class PrimeCounterExecutor {
    public static class PrimeTask implements Callable<Integer> {
        private final int start;
        private final int end;

        public PrimeTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Integer call() {
            int count = 0;
            for (int i = start; i <= end; i++) {
                if (isPrime(i)) {
                    count++;
                }
            }
            return count;
        }

        private boolean isPrime(int n) {
            if (n < 2)
                return false;
            for (int i = 2; i * i <= n; i++) {
                if (n % i == 0)
                    return false;
            }
            return true;
        }
    }

    private int N = 1_000_000; // интервал поиска: [2, N]
    private int CHUNK_SIZE = 1000; // размер батча
    private int NUM_WORKERS = Runtime.getRuntime().availableProcessors(); // число воркеров по умолчанию

    public PrimeCounterExecutor(int N, int CHUNK_SIZE, int NUM_WORKERS) {
        this.N = N;
        this.CHUNK_SIZE = CHUNK_SIZE;
        this.NUM_WORKERS = NUM_WORKERS;
    }

    public int countPrimes() throws Exception {
        System.out.println("Подсчет простых чисел в диапазоне [2, " + N + "] с CHUNK_SIZE = " + CHUNK_SIZE + " и "
                + NUM_WORKERS + " воркерами.");
        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(NUM_WORKERS);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 2; i <= N; i += CHUNK_SIZE) {
            int end = Math.min(i + CHUNK_SIZE - 1, N);
            futures.add(executor.submit(new PrimeTask(i, end)));
        }

        int totalPrimes = 0;
        for (Future<Integer> future : futures) {
            totalPrimes += future.get();
        }

        executor.shutdown();
        long endTime = System.currentTimeMillis();
        long elapsed = endTime - startTime;

        System.out.println("Найдено простых чисел в диапазоне [2, " + N + "]: " + totalPrimes);
        System.out.println("Время выполнения: " + elapsed + " мс");

        return totalPrimes;
    }
}
