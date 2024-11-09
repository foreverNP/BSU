package restaurant.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import restaurant.models.Menu.MenuItem;

public class Order {
    public static class Status {
        private int id;
        private String status;

        public Status(int id, String status) {
            this.id = id;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class OrderItem {
        private MenuItem menuItem;
        private int quantity;

        public OrderItem(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = quantity;
        }

        public MenuItem getMenuItem() {
            return menuItem;
        }

        public int getQuantity() {
            return quantity;
        }

        @Override
        public String toString() {
            return menuItem.getName() + " x" + quantity + " = "
                    + menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    private int id;
    private Client client;
    private Status status;
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(int id, Client client, Status status) {
        this.id = id;
        this.client = client;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            totalPrice = totalPrice.add(item.getMenuItem().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return totalPrice;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== Order ==========\n");
        sb.append("Client: ").append(client.getName()).append("\n");
        sb.append("Order ID: ").append(id).append("\n");
        sb.append("Items:\n");

        for (OrderItem item : orderItems) {
            sb.append(item.toString()).append("\n");
        }

        sb.append("---------------------------\n");
        sb.append("Total: ").append(getTotalPrice()).append("\n");
        sb.append("Status: ").append(status.getStatus()).append("\n");
        sb.append("===========================\n");

        return sb.toString();
    }
}
