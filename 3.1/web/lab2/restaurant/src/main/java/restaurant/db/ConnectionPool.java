package restaurant.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.InputStream;

import restaurant.exceptions.DBExeption;

public class ConnectionPool {

    private static final Logger logger = LogManager.getLogger(ConnectionPool.class);
    private static final int MAX_POOL_SIZE = 10;
    private static Queue<Connection> connectionPool = new LinkedList<>();
    private static Lock lock = new ReentrantLock();
    private static Properties dbProperties = new Properties();

    static {
        try (InputStream input = ConnectionPool.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                dbProperties.load(input);
            } else {
                throw new RuntimeException("Cannot find database.properties file.");
            }
        } catch (Exception e) {
            logger.error("Error loading database properties: ", e);
            throw new RuntimeException("Error loading database properties", e);
        }
    }

    // Получение соединения из пула
    public static Connection getConnection() throws DBExeption {
        lock.lock();
        try {
            if (!connectionPool.isEmpty()) {
                logger.info("Reusing connection from pool.");
                return connectionPool.poll();
            } else if (connectionPool.size() < MAX_POOL_SIZE) {
                logger.info("Creating new connection.");
                return createNewConnection();
            } else {
                throw new DBExeption("Maximum pool size reached, no available connections.");
            }
        } catch (SQLException e) {
            throw new DBExeption("Error creating new connection", e);
        } finally {
            lock.unlock();
        }
    }

    // Возврат соединения в пул
    public static void releaseConnection(Connection connection) {
        lock.lock();
        try {
            if (connection != null && !connection.isClosed()) {
                if (connectionPool.size() < MAX_POOL_SIZE) {
                    connectionPool.offer(connection);
                    logger.debug("Connection returned to pool.");
                } else {
                    connection.close();
                    logger.debug("Connection closed. Pool is full.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error while releasing connection: ", e);
        } finally {
            lock.unlock();
        }
    }

    // Создание нового соединения
    private static Connection createNewConnection() throws SQLException {
        String url = dbProperties.getProperty("url");
        String username = dbProperties.getProperty("username");
        String password = dbProperties.getProperty("password");
        return DriverManager.getConnection(url, username, password);
    }
}
