package restaurant.models;

import java.math.BigDecimal;

public class Bill {
    private Order order;

    public Bill(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== Bill ==========\n");
        sb.append(order.toString()).append("\n");
        sb.append("---------------------------\n");
        sb.append("Total: ").append(order.getTotalPrice()).append(" USD\n");
        sb.append("===========================\n");

        return sb.toString();
    }

    public boolean pay(BigDecimal amount) {
        if (amount.compareTo(order.getTotalPrice()) >= 0) {
            System.out.println("Payment successful!");
            return true;
        } else {
            System.out.println("Insufficient amount. Payment failed.");
            return false;
        }
    }
}
