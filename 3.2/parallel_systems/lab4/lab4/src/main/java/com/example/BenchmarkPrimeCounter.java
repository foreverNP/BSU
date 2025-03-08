package com.example;

public class BenchmarkPrimeCounter {
    public static void main(String[] args) throws Exception {

        int[] intervals = { 1_000_000, 10_000_000, 100_000_000 };
        int[] chunkSizes = { 500, 1000, 5000, 10000 };
        int[] workerCounts = { 1, 2, 4, Runtime.getRuntime().availableProcessors() };

        for (int N : intervals) {
            System.out.println("Benchmarking for N = " + N
                    + "-----------------------------------------------------------------------");
            for (int CHUNK_SIZE : chunkSizes) {
                for (int NUM_WORKERS : workerCounts) {
                    PrimeCounterExecutor primeCounter = new PrimeCounterExecutor(N, CHUNK_SIZE, NUM_WORKERS);
                    int count = primeCounter.countPrimes();
                }
            }
        }

    }
}
