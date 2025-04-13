package lab7;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

public class ParkingLotSynchronized {
    private static final int PARKING_CAPACITY = 5; // Количество мест на стоянке
    private static final int CARS_COUNT = 10; // Количество автомобилей
    private static final int MAX_PARKING_TIME = 3; // Максимальное время стоянки в секундах
    private static final int MAX_DRIVING_TIME = 5; // Максимальное время вождения в секундах

    // Массив парковочных мест: 0 - свободно, иначе - ID припаркованного автомобиля
    private final int[] parkingSpots = new int[PARKING_CAPACITY];

    public synchronized int parkCar(int carId) {
        for (int i = 0; i < parkingSpots.length; i++) {
            if (parkingSpots[i] == 0) {
                parkingSpots[i] = carId;
                System.out.println("Автомобиль #" + carId + " припарковался на месте " + (i + 1));
                printParkingStatus();
                return i;
            }
        }
        System.out.println("Автомобиль #" + carId + " не нашел свободного места");
        return -1;
    }

    public synchronized void leaveParkingLot(int carId, int spotIndex) {
        if (spotIndex >= 0 && spotIndex < parkingSpots.length && parkingSpots[spotIndex] == carId) {
            parkingSpots[spotIndex] = 0;
            System.out.println("Автомобиль #" + carId + " покинул место " + (spotIndex + 1));
            printParkingStatus();
            notify();
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
                + " мест");
    }

    static class Car extends Thread {
        private final int carId;
        private final ParkingLotSynchronized parkingLot;
        private final Random random = new Random();
        private int parkedSpotIndex = -1; // Индекс места, где припаркован автомобиль (-1 если не припаркован)

        public Car(int carId, ParkingLotSynchronized parkingLot) {
            this.carId = carId;
            this.parkingLot = parkingLot;
            setName("Car-" + carId);
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    synchronized (parkingLot) {
                        while ((parkedSpotIndex = parkingLot.parkCar(carId)) == -1) {
                            System.out.println("Автомобиль #" + carId + " ожидает освобождения места...");
                            parkingLot.wait(); // Ожидаем, пока не освободится место
                        }
                    }

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
            } catch (InterruptedException e) {
                System.out.println("Автомобиль #" + carId + " завершил работу");
                // Если прерываем авто, когда оно на парковке, освободим место
                if (parkedSpotIndex != -1) {
                    parkingLot.leaveParkingLot(carId, parkedSpotIndex);
                }
            }
        }
    }

    public static void main(String[] args) {
        ParkingLotSynchronized parkingLot = new ParkingLotSynchronized();
        List<Car> cars = new ArrayList<>();

        System.out.println("Запуск симуляции автостоянки с использованием synchronized");
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

        for (Car car : cars) {
            car.interrupt();
            try {
                car.join(1000); // Даем потоку секунду на завершение
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Программа завершена");
    }
}