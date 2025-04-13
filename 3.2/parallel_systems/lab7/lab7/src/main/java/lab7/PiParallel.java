package lab7;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class PiParallel {
    private static final int NUM_POINTS = 100_000_000;
    private static final int NUM_PRODUCERS = 2;
    private static final int NUM_CONSUMERS = 4;
    private static final int QUEUE_CAPACITY = 1000;

    private static final Random random = new Random(100000000L);

    private static final Point POISON_PILL = new Point(2, 2);

    static class Point {
        double x, y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Point> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        AtomicLong insideCircle = new AtomicLong(0);
        AtomicLong producedCount = new AtomicLong(0);

        // Создаём потоки-производители
        Thread[] producers = new Thread[NUM_PRODUCERS];
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            producers[i] = new Thread(() -> {
                while (true) {
                    long current = producedCount.getAndIncrement();
                    if (current >= NUM_POINTS) {
                        break;
                    }
                    double x = random.nextDouble();
                    double y = random.nextDouble();
                    Point p = new Point(x, y);
                    try {
                        queue.put(p);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        // Создаём потоки-потребители
        Thread[] consumers = new Thread[NUM_CONSUMERS];
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumers[i] = new Thread(() -> {
                while (true) {
                    try {
                        Point p = queue.take();
                        if (p == POISON_PILL) {
                            break;
                        }
                        if (p.x * p.x + p.y * p.y <= 1) {
                            insideCircle.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            producers[i].start();
        }
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumers[i].start();
        }

        for (Thread producer : producers) {
            producer.join();
        }

        for (int i = 0; i < NUM_CONSUMERS; i++) {
            queue.put(POISON_PILL);
        }

        for (Thread consumer : consumers) {
            consumer.join();
        }
        long endTime = System.currentTimeMillis();

        double piEstimate = 4.0 * insideCircle.get() / NUM_POINTS;

        System.out.println("Количество потоков производителей: " + NUM_PRODUCERS);
        System.out.println("Количество потоков потребителей: " + NUM_CONSUMERS);

        System.out.println("Приближенное значение pi: " + piEstimate);
        System.out.println("Точное значение pi: " + Math.PI);
        System.out.printf("Погрешность: %.10f%n", Math.abs(piEstimate - Math.PI));
        System.out.println("Время выполнения (мс): " + (endTime - startTime));
    }
}
