package restaurant.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@NamedQueries({
        @NamedQuery(name = "MenuItem.findAll", query = "SELECT m FROM MenuItem m"),
        @NamedQuery(name = "MenuItem.findById", query = "SELECT m FROM MenuItem m WHERE m.id = :id"),
})
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "category", nullable = false)
    private String category;

    public static final String mainCourseCategory = "main course";
    public static final String dessertCategory = "dessert";
    public static final String drinkCategory = "drink";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("========== Menu Item ==========\n")
                .append("ID: ").append(id)
                .append("\tName: ").append(name)
                .append("\tPrice: ").append(price)
                .append("\tCategory: ").append(category).append("\n")
                .append("===============================\n")
                .toString();
    }
}