package lab7;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.System.out;

public class ParkingLotSemaphore {
    private static final int PARKING_CAPACITY = 5;
    private static final int CARS_COUNT = 10;
    private static final int MAX_PARKING_TIME = 3;
    private static final int MAX_DRIVING_TIME = 5;

    private final int[] parkingSpots = new int[PARKING_CAPACITY];

    // для контроля доступа к парковочным местам
    private final Semaphore semaphore = new Semaphore(PARKING_CAPACITY, true);
    // для синхронизации доступа к массиву parkingSpots
    private final Lock spotsLock = new ReentrantLock();

    public int parkCar(int carId) throws InterruptedException {
        semaphore.acquire();

        spotsLock.lock();
        try {

            for (int i = 0; i < parkingSpots.length; i++) {
                if (parkingSpots[i] == 0) {
                    parkingSpots[i] = carId;
                    System.out.println("Автомобиль #" + carId + " припарковался на месте " + (i + 1));
                    printParkingStatus();
                    return i;
                }
            }

            // Эта ситуация не должна произойти, так как семафор гарантирует наличие
            // свободного места
            System.out.println("ОШИБКА: Автомобиль #" + carId + " получил разрешение, но все места заняты");
            semaphore.release();
            return -1;
        } finally {
            spotsLock.unlock();
        }
    }

    public void leaveParkingLot(int carId, int spotIndex) {
        spotsLock.lock();
        try {
            if (spotIndex >= 0 && spotIndex < parkingSpots.length && parkingSpots[spotIndex] == carId) {
                parkingSpots[spotIndex] = 0;
                System.out.println("Автомобиль #" + carId + " покинул место " + (spotIndex + 1));
                printParkingStatus();

                semaphore.release();
            }
        } finally {
            spotsLock.unlock();
        }
    }

    private void printParkingStatus() {
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";
        final String RED = "\u001B[31m";

        int occupiedCount = 0;
        StringBuilder status = new StringBuilder("Статус мест: [");

        for (int i = 0; i < parkingSpots.length; i++) {
            if (parkingSpots[i] != 0) {
                status.append(RED).append("Место ").append(i + 1).append(": авто #").append(parkingSpots[i])
                        .append(RESET);
                occupiedCount++;
            } else {
                status.append(GREEN).append("Место ").append(i + 1).append(": свободно").append(RESET);
            }

            if (i < parkingSpots.length - 1) {
                status.append(", ");
            }
        }
        status.append("]");

        out.println(status);
        out.println("Статус стоянки: занято " + RED + occupiedCount + RESET + " из " + GREEN + PARKING_CAPACITY + RESET
                + " мест (доступно разрешений: " + GREEN + semaphore.availablePermits() + RESET + ")");
    }

    static class Car extends Thread {
        private final int carId;
        private final ParkingLotSemaphore parkingLot;
        private final Random random = new Random();
        private int parkedSpotIndex = -1;

        public Car(int carId, ParkingLotSemaphore parkingLot) {
            this.carId = carId;
            this.parkingLot = parkingLot;
            setName("Car-" + carId);
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    // Пытаемся припарковаться
                    System.out.println("Автомобиль #" + carId + " ищет место для парковки...");
                    parkedSpotIndex = parkingLot.parkCar(carId);

                    if (parkedSpotIndex >= 0) {
                        // Стоим на парковке
                        int parkingTime = random.nextInt(MAX_PARKING_TIME) + 1;
                        System.out.println("Автомобиль #" + carId + " будет стоять " + parkingTime + " сек.");
                        TimeUnit.SECONDS.sleep(parkingTime);

                        // Покидаем парковку
                        parkingLot.leaveParkingLot(carId, parkedSpotIndex);
                        parkedSpotIndex = -1;

                        // Ездим по городу
                        int drivingTime = random.nextInt(MAX_DRIVING_TIME) + 1;
                        System.out.println("Автомобиль #" + carId + " уехал на " + drivingTime + " сек.");
                        TimeUnit.SECONDS.sleep(drivingTime);
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
        ParkingLotSemaphore parkingLot = new ParkingLotSemaphore();
        List<Car> cars = new ArrayList<>();

        System.out.println("Запуск симуляции автостоянки с использованием семафора");
        System.out.println("Количество мест на стоянке: " + PARKING_CAPACITY);
        System.out.println("Количество автомобилей: " + CARS_COUNT);

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