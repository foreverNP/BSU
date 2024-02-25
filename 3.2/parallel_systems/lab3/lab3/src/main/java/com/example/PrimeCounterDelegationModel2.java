package com.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrimeCounterDelegationModel2 {
    private static final Queue<RangeTask> taskQueue = new LinkedList<>();
    private static final Lock queueLock = new ReentrantLock();
    private static final Condition notEmpty = queueLock.newCondition();
    private static final AtomicInteger primeCount = new AtomicInteger(0);

    private static final int CHUNK_SIZE = 1000;

    public static void main(String[] args) throws Exception {
        int N = 1_000_000;
        if (args.length > 0) {
            try {
                N = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Некорректное число, используется значение по умолчанию: " + N);
            }
        }

        int numWorkers = Runtime.getRuntime().availableProcessors();
        List<Thread> workers = new ArrayList<>();

        for (int i = 0; i < numWorkers; i++) {
            Thread worker = new Thread(new Worker());
            worker.start();
            workers.add(worker);
        }

        queueLock.lock();
        try {
            for (int i = 2; i <= N; i += CHUNK_SIZE) {
                int end = Math.min(i + CHUNK_SIZE - 1, N);
                taskQueue.add(new RangeTask(i, end));
            }
            notEmpty.signalAll();
        } finally {
            queueLock.unlock();
        }

        queueLock.lock();
        try {
            for (int i = 0; i < numWorkers; i++) {
                taskQueue.add(new RangeTask(-1, -1));
            }
            notEmpty.signalAll();
        } finally {
            queueLock.unlock();
        }

        for (Thread worker : workers) {
            worker.join();
        }

        System.out.println("Количество простых чисел от 2 до " + N + " = " + primeCount.get());
    }

    static class RangeTask {
        final int start;
        final int end;

        RangeTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        boolean isTerminationSignal() {
            return start == -1 && end == -1;
        }
    }

    static class Worker implements Runnable {
        @Override
        public void run() {
            while (true) {
                RangeTask task;
                queueLock.lock();
                try {
                    while (taskQueue.isEmpty()) {
                        try {
                            notEmpty.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    task = taskQueue.poll();
                } finally {
                    queueLock.unlock();
                }

                if (task.isTerminationSignal()) {
                    break;
                }

                int count = 0;
                for (int i = task.start; i <= task.end; i++) {
                    if (isPrime(i)) {
                        count++;
                    }
                }
                primeCount.addAndGet(count);
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
