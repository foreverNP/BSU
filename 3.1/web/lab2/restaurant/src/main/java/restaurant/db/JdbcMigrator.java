package restaurant.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

public class JdbcMigrator {

    private static final Logger logger = LogManager.getLogger(JdbcMigrator.class);

    public static void migrate() {
        try (Connection connection = JdbcConnector.getConnection()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(JdbcMigrator.class.getClassLoader().getResourceAsStream("migration.sql")));
                    Statement stmt = connection.createStatement()) {

                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sql.append(line);
                    if (line.trim().endsWith(";")) {
                        stmt.execute(sql.toString());
                        sql.setLength(0);
                    }
                }
                logger.info("Database migrated successfully.");
            }
        } catch (Exception e) {
            logger.error("Error migrating database: ", e);
            throw new RuntimeException("Error migrating database", e);
        }
    }
}
