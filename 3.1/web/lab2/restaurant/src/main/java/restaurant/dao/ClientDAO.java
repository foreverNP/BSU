package restaurant.dao;

import restaurant.builders.DBBuilder;
import restaurant.db.ConnectionPool;
import restaurant.exceptions.DAOException;
import restaurant.models.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    private static final String SELECT_ALL_CLIENTS = "SELECT id, name, balance FROM clients";
    private static final String SELECT_CLIENT_BY_ID = "SELECT id, name, balance FROM clients WHERE id = ?";
    private static final String UPDATE_CLIENT_BALANCE = "UPDATE clients SET balance = ? WHERE id = ?";
    private static final String INSERT_CLIENT = "INSERT INTO clients (name, balance) VALUES (?, ?)";

    // Получение всех клиентов
    public List<Client> getAllClients() throws DAOException {
        List<Client> clients = new ArrayList<>();
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_CLIENTS)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Client client = DBBuilder.buildClient(rs);
                    clients.add(client);
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error getting all clients", e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection); // Возвращаем соединение в пул
            }
        }
        return clients;
    }

    // Получение клиента по ID
    public Client getClientById(int clientId) throws DAOException {
        Client client = null;
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(SELECT_CLIENT_BY_ID)) {
                stmt.setInt(1, clientId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    client = DBBuilder.buildClient(rs);
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error getting client by ID: " + clientId, e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection); // Возвращаем соединение в пул
            }
        }
        return client;
    }

    // Обновление баланса клиента
    public boolean updateClientBalance(int clientId, double newBalance) throws DAOException {
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(UPDATE_CLIENT_BALANCE)) {
                stmt.setBigDecimal(1, new java.math.BigDecimal(newBalance));
                stmt.setInt(2, clientId);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error updating client balance for ID: " + clientId, e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection); // Возвращаем соединение в пул
            }
        }
        return false;
    }

    // Добавление нового клиента
    public boolean addClient(Client client) throws DAOException {
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(INSERT_CLIENT)) {
                stmt.setString(1, client.getName());
                stmt.setBigDecimal(2, new java.math.BigDecimal(client.getBalance().doubleValue()));
                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error adding new client: " + client.getName(), e);
        } finally {
            if (connection != null) {
                ConnectionPool.releaseConnection(connection); // Возвращаем соединение в пул
            }
        }
        return false;
    }
}
