package restaurant.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NamedQueries({
        @NamedQuery(name = "Order.findAllByClientId", query = "SELECT o FROM Order o WHERE o.client.id = :clientId"),
        @NamedQuery(name = "Order.findById", query = "SELECT o FROM Order o WHERE o.id = :id"),
        @NamedQuery(name = "Order.findAll", query = "SELECT o FROM Order o"),
        @NamedQuery(name = "Order.updateStatus", query = "UPDATE Order o SET o.status = :status WHERE o.id = :id"),
})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public static final String pendingStatus = "pending";
    public static final String cookingStatus = "cooking";
    public static final String cookedStatus = "cooked";
    public static final String completedStatus = "completed";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this); // Связываем двусторонне
    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            totalPrice = totalPrice.add(item.getPrice());
        }
        return totalPrice;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== Order ==========\n")
                .append("ID: ").append(id).append("\n")
                .append("Client: ").append(client != null ? client.getName() : "null").append("\n")
                .append("Status: ").append(status).append("\n")
                .append("Items: \n");
        for (OrderItem item : orderItems) {
            sb.append(item.toString()).append("\n");
        }
        sb.append("Total Price: ").append(getTotalPrice()).append("\n")
                .append("===========================\n");
        return sb.toString();
    }
}
