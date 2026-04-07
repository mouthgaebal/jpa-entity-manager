package persistence;

import java.sql.Connection;
import java.sql.SQLException;

public class EntityManager implements AutoCloseable {

    private final Connection connection;
    private final Transaction transaction;

    public EntityManager(Connection connection) {
        this.connection = connection;
        transaction = new Transaction(connection);
    }

    public PersistenceContext getPersistenceContext() {
        return new PersistenceContext();
    }

    public Transaction getTransaction() {
        return transaction;
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
