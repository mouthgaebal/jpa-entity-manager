package persistence;

import annotation.Id;
import util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SimpleEntityManager implements AutoCloseable {

    private final List<Object> persistTargets = new LinkedList<>();

    private final Connection connection;
    private final PersistenceContext persistenceContext;
    private final Transaction transaction;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;

    public SimpleEntityManager(Connection connection) {
        this.connection = connection;
        this.persistenceContext = new PersistenceContext();
        this.transaction = new Transaction(connection);
        this.entityLoader = new EntityLoader(connection);
        this.entityPersister = new EntityPersister(connection);
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

    public Transaction getTransaction() {
        return transaction;
    }

    public void persist(Object entity) {
        persistTargets.add(entity);
    }

    public void flush() {
        for (Object target : persistTargets) {
            final Object idValue = extractIdValue(target);
            if (idValue == null) {
                final Object id = entityPersister.insert(target);
                persistenceContext.put(target.getClass(), new EntityId(id), target);
                continue;
            }

            final Object cache = persistenceContext.get(target.getClass(), new EntityId(idValue));
            if (cache != null) {
                continue;
            }

            final Object id = entityPersister.insert(target);
            persistenceContext.put(target.getClass(), new EntityId(id), target);
        }
    }

    private Object extractIdValue(Object target) {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Id.class)) {
                continue;
            }
            return ReflectionUtils.getValue(field, target);
        }
        return null;
    }

}
