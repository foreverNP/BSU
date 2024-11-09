package restaurant.mock;

import java.util.ArrayList;
import java.util.List;

public class KitchenSubject {
    public interface KitchenObserver {
        void onOrderPrepared(int orderId);
    }

    private final List<KitchenObserver> observers = new ArrayList<>();

    public void addObserver(KitchenObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(KitchenObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(int orderId) {
        for (KitchenObserver observer : observers) {
            observer.onOrderPrepared(orderId);
        }
    }
}
