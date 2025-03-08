package com.example;

import java.util.concurrent.atomic.AtomicInteger;

public class PrimeCounterDelegationModel1 {
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    private static int availableThreads = MAX_THREADS;
    private static final Object mutex = new Object();

    private static final int CHUNK_SIZE = 1000;

    private static final AtomicInteger primeCount = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        int N = 1_000_000;
        if (args.length > 0) {
            try {
                N = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Некорректное число, используется значение по умолчанию: " + N);
            }
        }

        for (int i = 2; i <= N; i += CHUNK_SIZE) {
            synchronized (mutex) {
                // Тут можно было бы использовать if, так как у нас только один поток
                // уменьшает availableThreads и уведомление от другого потока будет значить
                // увеличение availableThreads, но на всякий случай используем while
                while (availableThreads == 0) {
                    mutex.wait();
                }
                availableThreads--;
            }

            Thread t = new Thread(new PrimeTask(i, Math.min(i + CHUNK_SIZE - 1, N)));
            t.start();
        }

        // Ожидаем завершения всех потоков
        synchronized (mutex) {
            while (availableThreads != MAX_THREADS) {
                mutex.wait();
            }
        }

        System.out.println("Количество простых чисел от 2 до " + N + " = " + primeCount.get());
    }

    public static void incrementThreadAvailability() {
        synchronized (mutex) {
            availableThreads++;
            mutex.notifyAll();
        }
    }

    static class PrimeTask implements Runnable {
        private final int start;
        private final int end;

        PrimeTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            // Предположим, что произошло исключение во время выполнения задачи
            // и мы не уменьшили счетчик доступных потоков. В этом случае программа никогда
            // не завершится, так как главный поток будет ждать, пока счетчик доступных
            // потоков не станет равным MAX_THREADS. Поэтому увеличиваем счетчик доступных
            // потоков в finally
            try {
                int count = 0;
                for (int i = start; i <= end; i++) {
                    if (isPrime(i)) {
                        count++;
                    }
                }
                primeCount.addAndGet(count);
            } catch (Exception e) {
                System.err.println("Exception in PrimeTask: " + e.getMessage());
            } finally {
                incrementThreadAvailability();
            }
        }

        private boolean isPrime(int n) {
            if (n < 2) {
                return false;
            }
            for (int i = 2; i * i <= n; i++) {
                if (n % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }
}
