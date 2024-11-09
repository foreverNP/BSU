package restaurant.builders;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import restaurant.models.Client;
import restaurant.models.Menu;
import restaurant.models.Order;

public class DBBuilder {
    public static Client buildClient(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        BigDecimal balance = rs.getBigDecimal("balance");
        return new Client(id, name, balance);
    }

    public static Menu.Category buildCategory(ResultSet rs) throws SQLException {
        int id = rs.getInt("category_id");
        String name = rs.getString("category_name");
        return new Menu.Category(id, name);
    }

    public static Order.Status buildStatus(ResultSet rs) throws SQLException {
        int id = rs.getInt("status_id");
        String status = rs.getString("status_name");
        return new Order.Status(id, status);
    }

    public static Menu.MenuItem buildMenuItem(ResultSet rs) throws SQLException {
        int id = rs.getInt("menu_item_id");
        String name = rs.getString("name");
        BigDecimal price = rs.getBigDecimal("price");

        return new Menu.MenuItem(id, name, price, buildCategory(rs));
    }

    public static Menu buildMenu(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        while (rs.next()) {
            menu.addMenuItem(buildMenuItem(rs));
        }
        return menu;
    }

    public static Order.OrderItem buildOrderItem(ResultSet rs) throws SQLException {
        int quantity = rs.getInt("quantity");

        return new Order.OrderItem(buildMenuItem(rs), quantity);
    }

    public static Order buildOrder(ResultSet rs, Client client) throws SQLException {
        int id = rs.getInt("id");

        return new Order(id, client, buildStatus(rs));
    }
}
