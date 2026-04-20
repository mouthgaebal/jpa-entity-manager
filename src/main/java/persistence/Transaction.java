package persistence;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction {

    private final Connection connection;

    public Transaction(Connection connection) {
        this.connection = connection;
    }

    public void begin() {
        try {
            if (!connection.getAutoCommit()) {
                return;
            }
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void commit() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void rollback() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
