package lab7;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.System.out;

public class ParkingLotCondition {
    private static final int PARKING_CAPACITY = 5;
    private static final int CARS_COUNT = 10;
    private static final int MAX_PARKING_TIME = 3;
    private static final int MAX_DRIVING_TIME = 5;

    private final int[] parkingSpots = new int[PARKING_CAPACITY];

    private int occupiedSpots = 0;

    // Блокировка для синхронизации доступа к состоянию парковки
    private final Lock lock = new ReentrantLock();

    // Условная переменная для ожидания освобождения места
    private final Condition spotAvailable = lock.newCondition();

    public int parkCar(int carId) throws InterruptedException {
        lock.lock();
        try {
            // Ждем, пока не появится свободное место
            while (occupiedSpots >= PARKING_CAPACITY) {
                System.out.println("Автомобиль #" + carId + " ожидает освобождения места...");
                spotAvailable.await();
            }

            for (int i = 0; i < parkingSpots.length; i++) {
                if (parkingSpots[i] == 0) {
                    parkingSpots[i] = carId;
                    occupiedSpots++;
                    System.out.println("Автомобиль #" + carId + " припарковался на месте " + (i + 1));
                    printParkingStatus();
                    return i;
                }
            }

            System.out.println(
                    "ОШИБКА: Автомобиль #" + carId + " не нашел свободного места, хотя счетчик показывает наличие");
            return -1;
        } finally {
            lock.unlock();
        }
    }

    public void leaveParkingLot(int carId, int spotIndex) {
        lock.lock();
        try {
            if (spotIndex >= 0 && spotIndex < parkingSpots.length && parkingSpots[spotIndex] == carId) {
                parkingSpots[spotIndex] = 0;
                occupiedSpots--;
                System.out.println("Автомобиль #" + carId + " покинул место " + (spotIndex + 1));
                printParkingStatus();

                spotAvailable.signal();
            }
        } finally {
            lock.unlock();
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

    static class Car extends Thread {
        private final int carId;
        private final ParkingLotCondition parkingLot;
        private final Random random = new Random();
        private int parkedSpotIndex = -1;

        public Car(int carId, ParkingLotCondition parkingLot) {
            this.carId = carId;
            this.parkingLot = parkingLot;
            setName("Car-" + carId);
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    System.out.println("Автомобиль #" + carId + " ищет место для парковки...");
                    parkedSpotIndex = parkingLot.parkCar(carId);

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
        ParkingLotCondition parkingLot = new ParkingLotCondition();
        List<Car> cars = new ArrayList<>();

        System.out.println("Запуск симуляции автостоянки с использованием условных переменных");
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