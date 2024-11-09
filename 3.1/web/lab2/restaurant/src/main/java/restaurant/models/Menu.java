package restaurant.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    public static class Category {
        private int id;
        private String name;

        public Category(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static class MenuItem {
        private int id;
        private String name;
        private BigDecimal price;
        private Category category;

        public MenuItem(int id, String name, BigDecimal price, Category category) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.category = category;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public Category getCategory() {
            return category;
        }

        @Override
        public String toString() {
            return String.format("%d|%s (%s): %s USD", id, name, category.getName(), price);
        }
    }

    private List<MenuItem> menuItems = new ArrayList<>();

    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== Menu ==========\n");

        for (MenuItem item : menuItems) {
            sb.append(item.toString()).append("\n");
        }

        sb.append("===========================\n");

        return sb.toString();
    }
}