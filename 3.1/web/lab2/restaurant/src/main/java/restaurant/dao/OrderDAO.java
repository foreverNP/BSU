package restaurant.dao;

import restaurant.builders.DBBuilder;
import restaurant.db.ConnectionPool;
import restaurant.exceptions.DAOException;
import restaurant.models.Order;
import restaurant.models.Client;
import restaurant.models.Order.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private static final String SELECT_ALL_ORDERS_BY_CLIENT = "SELECT o.id, o.client_id, o.status_id, s.status AS status_name "
            + "FROM orders o " +
            "JOIN statuses s ON o.status_id = s.id " +
            "WHERE o.client_id = ?";

    private static final String SELECT_ORDER_ITEMS_BY_ORDER_ID = "SELECT oi.menu_item_id, oi.quantity, mi.name, mi.price, mi.category_id, c.category AS category_name "
            + "FROM order_items oi " +
            "JOIN menu_items mi ON oi.menu_item_id = mi.id " +
            "JOIN categories c ON mi.category_id = c.id " +
            "WHERE oi.order_id = ?";

    private static final String SELECT_ORDER_BY_ID = "SELECT o.id, o.client_id, o.status_id, s.status AS status_name " +
            "FROM orders o " +
            "JOIN statuses s ON o.status_id = s.id " +
            "WHERE o.id = ?";

    private static final String INSERT_ORDER = "INSERT INTO orders (client_id, status_id) VALUES (?, ?)";
    private static final String INSERT_ORDER_ITEM = "INSERT INTO order_items (order_id, menu_item_id, quantity) VALUES (?, ?, ?)";
    private static final String UPDATE_ORDER_STATUS = "UPDATE orders SET status_id = ? WHERE id = ?";

    public static final Integer STATUS_ID_PENDING = 1;
    public static final Integer STATUS_ID_COOKING = 2;
    public static final Integer STATUS_ID_COOKED = 3;
    public static final Integer STATUS_ID_COMPLETED = 4;

    // Получение заказов клиента с элементами заказа
    public List<Order> getOrdersByClientId(int clientId) throws DAOException {
        List<Order> orders = new ArrayList<>();
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_ORDERS_BY_CLIENT)) {
                stmt.setInt(1, clientId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Client client = new ClientDAO().getClientById(clientId);
                    Order order = DBBuilder.buildOrder(rs, client);
                    List<OrderItem> orderItems = getOrderItemsByOrderId(order.getId());
                    order.setOrderItems(orderItems);
                    orders.add(order);
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error fetching orders by client ID: " + clientId, e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection);
            }
        }
        return orders;
    }

    // Получение элементов заказа
    private List<OrderItem> getOrderItemsByOrderId(int orderId) throws DAOException {
        List<OrderItem> orderItems = new ArrayList<>();
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(SELECT_ORDER_ITEMS_BY_ORDER_ID)) {
                stmt.setInt(1, orderId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    OrderItem orderItem = DBBuilder.buildOrderItem(rs);
                    orderItems.add(orderItem);
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error fetching order items for order ID: " + orderId, e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection);
            }
        }
        return orderItems;
    }

    // Получение заказа по ID
    public Order getOrderById(int orderId) throws DAOException {
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(SELECT_ORDER_BY_ID)) {
                stmt.setInt(1, orderId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Client client = new ClientDAO().getClientById(rs.getInt("client_id"));
                    Order order = DBBuilder.buildOrder(rs, client);
                    List<OrderItem> orderItems = getOrderItemsByOrderId(orderId);
                    order.setOrderItems(orderItems);
                    return order;
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error fetching order by ID: " + orderId, e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection);
            }
        }
        return null;
    }

    // Создание нового заказа с элементами
    public boolean createOrder(int clientId, List<OrderItem> items) throws DAOException {
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmtOrder = connection.prepareStatement(INSERT_ORDER,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {

                stmtOrder.setInt(1, clientId);
                stmtOrder.setInt(2, STATUS_ID_PENDING);
                int rowsInserted = stmtOrder.executeUpdate();

                if (rowsInserted > 0) {
                    ResultSet generatedKeys = stmtOrder.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);

                        for (OrderItem item : items) {
                            try (PreparedStatement stmtItem = connection.prepareStatement(INSERT_ORDER_ITEM)) {
                                stmtItem.setInt(1, orderId);
                                stmtItem.setInt(2, item.getMenuItem().getId());
                                stmtItem.setInt(3, item.getQuantity());
                                stmtItem.executeUpdate();
                            }
                        }
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error creating order for client ID: " + clientId, e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection);
            }
        }
        return false;
    }

    // Подтвердить заказ и изменить его статус
    public boolean confirmOrder(int orderId) throws DAOException {
        return updateOrderStatus(orderId, STATUS_ID_COOKING);
    }

    // Заказ готов к выдаче
    public boolean orderReady(int orderId) throws DAOException {
        return updateOrderStatus(orderId, STATUS_ID_COOKED);
    }

    // Оплатить заказ
    public boolean completeOrder(int orderId) throws DAOException {
        return updateOrderStatus(orderId, STATUS_ID_COMPLETED);
    }

    // Обновление статуса заказа
    private boolean updateOrderStatus(int orderId, int statusId) throws DAOException {
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(UPDATE_ORDER_STATUS)) {
                stmt.setInt(1, statusId);
                stmt.setInt(2, orderId);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error updating status for order ID: " + orderId, e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection);
            }
        }
        return false;
    }
}
