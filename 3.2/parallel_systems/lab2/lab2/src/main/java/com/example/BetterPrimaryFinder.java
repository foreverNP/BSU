package com.example;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class BetterPrimaryFinder {
    private final static int MAX = 100_000_000;
    private final static int CHUNK_SIZE = 1_000;

    private static AtomicInteger nextChunk = new AtomicInteger(2);
    private static AtomicInteger primeCount = new AtomicInteger(0);

    private static class CountPrimesThread extends Thread {
        @Override
        public void run() {
            while (true) {
                int start = nextChunk.getAndAdd(CHUNK_SIZE);
                if (start > MAX)
                    break;
                int end = Math.min(start + CHUNK_SIZE - 1, MAX);
                int count = countPrimes(start, end);
                primeCount.addAndGet(count);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int numberOfThreads = 0;
        while (numberOfThreads < 1 || numberOfThreads > 30) {
            System.out.print("How many threads do you want to use (from 1 to 30)? ");
            numberOfThreads = scanner.nextInt();
            if (numberOfThreads < 1 || numberOfThreads > 30)
                System.out.println("Please enter a number between 1 and 30!");
        }

        CountPrimesThread[] threads = new CountPrimesThread[numberOfThreads];
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new CountPrimesThread();
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].join();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Total primes: " + primeCount.get());
        System.out.println("Elapsed time: " + (elapsedTime / 1000.0) + " seconds.");
    }

    private static int countPrimes(int min, int max) {
        int count = 0;
        for (int i = min; i <= max; i++) {
            if (isPrime(i))
                count++;
        }
        return count;
    }

    private static boolean isPrime(int x) {
        if (x < 2)
            return false;
        int top = (int) Math.sqrt(x);
        for (int i = 2; i <= top; i++) {
            if (x % i == 0)
                return false;
        }
        return true;
    }
}
