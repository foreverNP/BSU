package lab7;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

public class ParkingLotSemaphore2 {
    private static final int PARKING_CAPACITY = 5;
    private static final int CARS_COUNT = 10;
    private static final int MAX_PARKING_TIME = 3;
    private static final int MAX_DRIVING_TIME = 5;
    private static final int MAX_WAITING_TIME = 2000;

    private final int[] parkingSpots = new int[PARKING_CAPACITY];
    private int occupiedSpots = 0;

    private final Semaphore semaphore = new Semaphore(PARKING_CAPACITY, true);

    public int parkCar(int carId, long maxWaitMillis) throws ParkingException {
        try {
            if (semaphore.tryAcquire(maxWaitMillis, TimeUnit.MILLISECONDS)) {
                synchronized (parkingSpots) {
                    for (int i = 0; i < parkingSpots.length; i++) {
                        if (parkingSpots[i] == 0) {
                            parkingSpots[i] = carId;
                            occupiedSpots++;
                            System.out.println("Автомобиль #" + carId + " припарковался на месте " + (i + 1));
                            printParkingStatus();
                            return i;
                        }
                    }

                    System.out.println("ОШИБКА: Автомобиль #" + carId
                            + " не нашел свободного места, хотя семафор разрешил парковку");
                    semaphore.release();
                    return -1;
                }
            } else {
                throw new ParkingException("время ожидания превышено");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ParkingException("поток был прерван");
        }
    }

    public void leaveParkingLot(int carId, int spotIndex) {
        synchronized (parkingSpots) {
            if (spotIndex >= 0 && spotIndex < parkingSpots.length && parkingSpots[spotIndex] == carId) {
                parkingSpots[spotIndex] = 0;
                occupiedSpots--;
                System.out.println("Автомобиль #" + carId + " покинул место " + (spotIndex + 1));
                printParkingStatus();

                semaphore.release();
            }
        }
    }

    private void printParkingStatus() {
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";
        final String RED = "\u001B[31m";

        StringBuilder status = new StringBuilder("Статус мест: [");

        for (int i = 0; i < parkingSpots.length; i++) {
            if (parkingSpots[i] != 0) {
                status.append(RED).append("Место ").append(i + 1).append(": авто #").append(parkingSpots[i])
                        .append(RESET);
            } else {
                status.append(GREEN).append("Место ").append(i + 1).append(": свободно").append(RESET);
            }

            if (i < parkingSpots.length - 1) {
                status.append(", ");
            }
        }
        status.append("]");

        out.println(status);
        out.println("Статус стоянки: занято " + RED + occupiedSpots + RESET + " из " + GREEN + PARKING_CAPACITY + RESET
                + " мест (свободно: " + GREEN + (PARKING_CAPACITY - occupiedSpots) + RESET + ")");
    }

    public static class ParkingException extends Exception {
        public ParkingException(String message) {
            super(message);
        }
    }

    static class Car extends Thread {
        private final int carId;
        private final ParkingLotSemaphore2 parkingLot;
        private final Random random = new Random();
        private int parkedSpotIndex = -1;

        public Car(int carId, ParkingLotSemaphore2 parkingLot) {
            this.carId = carId;
            this.parkingLot = parkingLot;
            setName("Car-" + carId);
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    System.out.println("Автомобиль #" + carId + " ищет место для парковки...");

                    try {
                        long waitTime = random.nextInt(1500) + 500;
                        parkedSpotIndex = parkingLot.parkCar(carId, waitTime);

                        if (parkedSpotIndex >= 0) {
                            int parkingTime = random.nextInt(MAX_PARKING_TIME) + 1;
                            System.out.println("Автомобиль #" + carId + " будет стоять " + parkingTime + " сек.");
                            TimeUnit.SECONDS.sleep(parkingTime);

                            parkingLot.leaveParkingLot(carId, parkedSpotIndex);
                            parkedSpotIndex = -1;

                            int drivingTime = random.nextInt(MAX_DRIVING_TIME) + 1;
                            System.out.println("Автомобиль #" + carId + " уехал на " + drivingTime + " сек.");
                            TimeUnit.SECONDS.sleep(drivingTime);
                        }
                    } catch (ParkingException e) {
                        System.err.println("Автомобиль #" + carId + " не смог припарковаться: " + e.getMessage());
                        System.out.println("Автомобиль #" + carId + " уехал на другую стоянку");

                        // Уезжаем на другую стоянку на некоторое время
                        TimeUnit.SECONDS.sleep(random.nextInt(MAX_DRIVING_TIME) + 1);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Автомобиль #" + carId + " завершил работу");
                if (parkedSpotIndex != -1) {
                    parkingLot.leaveParkingLot(carId, parkedSpotIndex);
                }
            }
        }
    }

    public static void main(String[] args) {
        ParkingLotSemaphore2 parkingLot = new ParkingLotSemaphore2();
        List<Car> cars = new ArrayList<>();

        System.out.println("Запуск симуляции автостоянки 2 с использованием семафора");
        System.out.println("Количество мест на стоянке: " + PARKING_CAPACITY);
        System.out.println("Количество автомобилей: " + CARS_COUNT);
        System.out.println("Максимальное время ожидания: " + MAX_WAITING_TIME + " мс");

        for (int i = 1; i <= CARS_COUNT; i++) {
            Car car = new Car(i, parkingLot);
            cars.add(car);
            car.start();
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Завершение работы автомобилей...");
        for (Car car : cars) {
            car.interrupt();
            try {
                car.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Программа завершена");
    }
}