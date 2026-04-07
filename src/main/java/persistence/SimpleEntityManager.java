package persistence;

import java.sql.Connection;
import java.sql.SQLException;

public class SimpleEntityManager implements AutoCloseable {

    private final Connection connection;

    public SimpleEntityManager(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() {
        try {
            if (connection.isClosed()) {
                return;
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
