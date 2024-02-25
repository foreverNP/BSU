package com.example;

public class PrimeCounterFlag {
    // Флаг прекращения вычислений. Объявление volatile гарантирует,
    // что изменение переменной в одном потоке будет сразу видно другому.
    private static volatile boolean stopRequested = false;

    public static void main(String[] args) throws Exception {
        Thread countingThread = new Thread(() -> {
            long count = 0;
            long number = 2;

            while (!stopRequested) {
                if (isPrime(number)) {
                    count++;
                }
                number++;
            }

            System.out.println("Найдено простых чисел: " + count);
        });

        countingThread.start();

        System.out.println("Вычисление простых чисел запущено. Нажмите Enter для остановки...");
        System.in.read();

        stopRequested = true;
        countingThread.join();
    }

    private static boolean isPrime(long n) {
        if (n < 2)
            return false;
        for (long i = 2; i * i <= n; i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }
}
