package com;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Benchmark {
    private static final int[] THREAD_COUNTS = { 1, 2, 4, 8 };
    private static final int[] DURATIONS_SEC = { 10 };
    private static final int[] INITIAL_SIZES = { 10000, 100000 };
    private static final int[] KEY_RANGES = { 1000, 10000 };
    private static final double[][] OP_PROBS = {
            { 0.8, 0.1, 0.1 },
            { 0.5, 0.25, 0.25 },
            { 0.9, 0.05, 0.05 },
    };

    public static void main(String[] args) throws InterruptedException {
        for (int threads : THREAD_COUNTS) {
            for (int duration : DURATIONS_SEC) {
                for (int initSize : INITIAL_SIZES) {
                    for (int keyRange : KEY_RANGES) {
                        for (double[] probs : OP_PROBS) {
                            double cProb = probs[0], aProb = probs[1], rProb = probs[2];
                            System.out.printf(
                                    "Threads=%d, Duration=%ds, InitSize=%d, KeyRange=%d, Ops(c/a/r)=%.2f/%.2f/%.2f\n",
                                    threads, duration, initSize, keyRange, cProb, aProb, rProb);
                            runConfig(new com.CoarseList<Integer>(), threads, duration, initSize, keyRange, cProb,
                                    aProb, rProb);
                            runConfig(new com.FineList<Integer>(), threads, duration, initSize, keyRange, cProb, aProb,
                                    rProb);
                            System.out.println();
                        }
                    }
                }
            }
        }
    }

    private static void runConfig(Set<Integer> set,
            int numThreads,
            int durationSec,
            int initialSize,
            int keyRange,
            double containsProb,
            double addProb,
            double removeProb) throws InterruptedException {

        Random rnd = new Random();
        for (int i = 0; i < initialSize; i++) {
            set.add(rnd.nextInt(keyRange));
        }

        AtomicLong ops = new AtomicLong();
        Thread[] threads = new Thread[numThreads];
        long endTime = System.nanoTime() + durationSec * 1_000_000_000L;

        for (int t = 0; t < numThreads; t++) {
            threads[t] = new Thread(() -> {
                Random r = new Random();
                while (System.nanoTime() < endTime) {
                    int key = r.nextInt(keyRange);
                    double p = r.nextDouble();
                    if (p < containsProb) {
                        set.contains(key);
                    } else if (p < containsProb + addProb) {
                        set.add(key);
                    } else {
                        set.remove(key);
                    }
                    ops.incrementAndGet();
                }
            });
            threads[t].start();
        }

        for (Thread th : threads) {
            th.join();
        }

        long totalOps = ops.get();
        System.out.printf("  %-10s: total ops=%8d, ops/sec=%.2f\n",
                set.getClass().getSimpleName(), totalOps, totalOps / (double) durationSec);
    }
}
