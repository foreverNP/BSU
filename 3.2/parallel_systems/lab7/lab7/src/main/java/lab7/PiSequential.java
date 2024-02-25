package lab7;

import java.util.Random;

public class PiSequential {
    public static void main(String[] args) {
        long iterations = 100_000_000;
        long pointsInCircle = 0;
        Random random = new Random(100000000L);

        long startTime = System.currentTimeMillis();
        for (long i = 0; i < iterations; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();

            if (x * x + y * y <= 1) {
                pointsInCircle++;
            }
        }
        double pi = 4.0 * pointsInCircle / iterations;

        long endTime = System.currentTimeMillis();

        System.out.println("Приближенное значение pi: " + pi);
        System.out.println("Точное значение pi: " + Math.PI);
        System.out.printf("Погрешность: %.10f%n", Math.abs(pi - Math.PI));
        System.out.println("Время выполнения (мс): " + (endTime - startTime));
    }
}