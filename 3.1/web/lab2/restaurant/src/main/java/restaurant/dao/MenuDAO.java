package restaurant.dao;

import restaurant.builders.DBBuilder;
import restaurant.db.ConnectionPool;
import restaurant.exceptions.DAOException;
import restaurant.models.Menu;
import restaurant.models.Menu.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuDAO {
    private static final String SELECT_ALL_MENU_ITEMS = "SELECT mi.id AS menu_item_id, mi.name, mi.price, mi.category_id, c.category AS category_name "
            + "FROM menu_items mi " +
            "JOIN categories c ON mi.category_id = c.id";

    private static final String SELECT_MENU_ITEM_BY_ID = "SELECT mi.id AS menu_item_id, mi.name, mi.price, mi.category_id, c.category AS category_name "
            + "FROM menu_items mi " +
            "JOIN categories c ON mi.category_id = c.id " +
            "WHERE mi.id = ?";

    private static final String INSERT_MENU_ITEM = "INSERT INTO menu_items (name, price, category_id) VALUES (?, ?, ?)";

    // Метод для получения всего меню
    public Menu getMenu() throws DAOException {
        Menu menu = new Menu();
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_MENU_ITEMS)) {
                ResultSet rs = stmt.executeQuery();
                menu = DBBuilder.buildMenu(rs);
            }
        } catch (Exception e) {
            throw new DAOException("Error fetching menu", e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection);
            }
        }
        return menu;
    }

    // Метод для получения элемента меню по ID
    public MenuItem getMenuItemById(int id) throws DAOException {
        MenuItem menuItem = null;
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(SELECT_MENU_ITEM_BY_ID)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    menuItem = DBBuilder.buildMenuItem(rs);
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error fetching menu item by ID: " + id, e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection);
            }
        }
        return menuItem;
    }

    // Метод для добавления нового элемента меню
    public boolean addMenuItem(MenuItem menuItem) throws DAOException {
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(INSERT_MENU_ITEM)) {
                stmt.setString(1, menuItem.getName());
                stmt.setDouble(2, menuItem.getPrice().doubleValue());
                stmt.setInt(3, menuItem.getCategory().getId());
                stmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            throw new DAOException("Error adding new menu item: " + menuItem.getName(), e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection);
            }
        }
    }
}
