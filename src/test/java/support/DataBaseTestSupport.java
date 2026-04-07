package support;

import database.DatabaseServer;
import database.H2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseTestSupport {

    private static final DatabaseServer DATABASE_SERVER;

    static {
        DATABASE_SERVER = createDatabaseServer();
    }

    private static H2 createDatabaseServer() {
        try {
            return new H2();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        try {
            DATABASE_SERVER.start();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @AfterAll
    static void afterAll() {
        DATABASE_SERVER.stop();
    }

    protected Connection getConnection() {
        try {
            return DATABASE_SERVER.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void truncateTable(String tableName) {
        final String sql = "TRUNCATE TABLE " + tableName;
        try (final Connection connection = getConnection();
             final var statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
