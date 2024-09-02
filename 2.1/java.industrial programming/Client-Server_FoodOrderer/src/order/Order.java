package order;

import menu.Dish;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Order implements Serializable {
    private static final long serialVersionUID = 3L;
    private static final int MAX_AMOUNT = 100;
    private static final int infoLength = 50;

    private String deliveryAddress;
    private Map<Dish, Integer> dishes;
    private double totalAmount;

    public Order(String deliveryAddress) throws IllegalArgumentException {
        if (deliveryAddress == null || deliveryAddress.isEmpty()) {
            throw new IllegalArgumentException("The delivery address cannot be empty");
        }
        this.deliveryAddress = deliveryAddress;
        this.dishes = new HashMap<>();
        this.totalAmount = 0.0;
    }

    public Order() {
        this.dishes = new HashMap<>();
        this.totalAmount = 0.0;
    }

    public void addDish(Dish dish, int quantity) throws IllegalArgumentException {
        if (quantity <= 0 || quantity >= 100) {
            throw new IllegalArgumentException("The number must be greater than 0 and less than " + MAX_AMOUNT);
        }

        dishes.merge(dish, quantity, Integer::sum);

        totalAmount += dish.getPrice() * quantity;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setDeliveryAddress(String deliveryAddress) throws IllegalArgumentException {
        if (deliveryAddress == null || deliveryAddress.isEmpty()) {
            throw new IllegalArgumentException("The delivery address cannot be empty");
        }
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public String toString() {
        StringBuilder orderString = new StringBuilder();

        orderString.append("-".repeat((infoLength - 5) / 2)).append("ORDER").append("-".repeat((infoLength - 5) / 2)).append("\n");

        orderString.append("Delivery Address: ").append(deliveryAddress).append("\n");

        orderString.append("-".repeat((infoLength - 6) / 2)).append("DISHES").append("-".repeat((infoLength - 6) / 2)).append("\n");

        orderString.append("Name                  Quantity          Subtotal  \n\n");
        for (Map.Entry<Dish, Integer> entry : dishes.entrySet()) {
            Dish dish = entry.getKey();
            int quantity = entry.getValue();

            String nameFormatted = String.format("%-22s", dish.getName());
            String quantityFormatted = String.format(" %-18s", quantity);
            String priceFormatted = String.format("%-7.2f$", dish.getPrice() * quantity);

            orderString.append(nameFormatted).append(quantityFormatted).append(priceFormatted).append("\n");
        }

        orderString.append("-".repeat(infoLength)).append("\n");
        orderString.append(String.format("%-41s%-7.2f$", "Total", totalAmount)).append("\n");
        orderString.append("-".repeat(infoLength)).append("\n");

        return orderString.toString();
    }
}
