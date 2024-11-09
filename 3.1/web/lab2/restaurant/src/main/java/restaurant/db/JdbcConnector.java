package restaurant.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class JdbcConnector {

    private static final Logger logger = LogManager.getLogger(JdbcConnector.class);
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try (InputStream input = JdbcConnector.class.getClassLoader().getResourceAsStream("database.properties")) {
                Properties properties = new Properties();
                if (input != null) {
                    properties.load(input);
                    String url = properties.getProperty("url");
                    String username = properties.getProperty("username");
                    String password = properties.getProperty("password");
                    connection = DriverManager.getConnection(url, username, password);
                    logger.info("New connection established.");
                } else {
                    throw new RuntimeException("Cannot find database.properties file.");
                }
            } catch (Exception e) {
                logger.error("Error establishing database connection: ", e);
                throw new SQLException("Error establishing database connection", e);
            }
        }
        return connection;
    }
}
