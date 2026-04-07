package persistence;

import jdbc.QueryExecutor;

import java.sql.Connection;
import java.sql.SQLException;

public class SimpleEntityManager implements AutoCloseable {

    private final Connection connection;
    private final PersistenceContext persistenceContext;
    private final EntityLoader entityLoader;

    public SimpleEntityManager(Connection connection) {
        this.connection = connection;
        this.persistenceContext = new PersistenceContext();
        this.entityLoader = new EntityLoader(new QueryExecutor(connection));
    }

    public Connection getConnection() {
        return connection;
    }

    public <T> T find(Class<T> entityClass, Long id) {
        final EntityId entityId = new EntityId(id);
        final T cached = persistenceContext.get(entityClass, entityId);
        if (cached != null) {
            return cached;
        }

        final T entity = entityLoader.load(entityClass, id);
        if (entity == null) {
            return null;
        }
        persistenceContext.put(entityClass, entityId, entity);
        return entity;
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
