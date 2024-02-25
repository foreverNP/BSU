package restaurant.mock;

import java.util.Random;

public class KitchenMock implements Runnable {
    private final int orderId;
    private final KitchenSubject kitchenSubject;

    public KitchenMock(int orderId, KitchenSubject kitchenSubject) {
        this.orderId = orderId;
        this.kitchenSubject = kitchenSubject;
    }

    @Override
    public void run() {
        prepareOrder(orderId);
    }

    public void prepareOrder(int orderId) {
        int cookingTime = new Random().nextInt(5000) + 2000;
        try {
            System.out.println("Order " + orderId + " is being prepared.");
            Thread.sleep(cookingTime);
            kitchenSubject.notifyObservers(orderId); // Уведомляем всех наблюдателей о готовности заказа
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
