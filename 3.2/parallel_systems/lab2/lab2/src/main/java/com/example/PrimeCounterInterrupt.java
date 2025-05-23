package com.example;

public class PrimeCounterInterrupt {
    public static void main(String[] args) throws Exception {
        Thread countingThread = new Thread(() -> {
            long count = 0;
            long number = 2;
            while (true) {
                if (Thread.interrupted()) {
                    System.out.println("Получено прерывание. Завершаем вычисления...");
                    break;
                }

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

        countingThread.interrupt();
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
