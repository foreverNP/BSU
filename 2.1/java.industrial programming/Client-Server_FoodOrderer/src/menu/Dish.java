package menu;

import java.io.Serializable;

public class Dish implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        DRINK,
        MAIN_COURSE,
        DESSERT,
        APPETIZER
    }

    private double price;
    private String name;
    private String description;
    private Type type;

    public Dish(double price, String name, String description, Type type) throws IllegalArgumentException {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }

        this.price = price;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        String nameFormatted = String.format("%-22s", name);
        String priceFormatted = String.format("%-7.2f$", price);
        String descriptionFormatted = String.format(" %-40s", description);

        return nameFormatted + priceFormatted + descriptionFormatted;
    }
}
