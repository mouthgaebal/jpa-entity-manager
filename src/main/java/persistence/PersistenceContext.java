package persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersistenceContext {

    private final Map<Class<?>, Map<EntityId, Object>> entities = new HashMap<>();

    public <T> T get(Class<T> entity, EntityId id) {
        Objects.requireNonNull(entity, "entity 는 필수 입니다.");
        Objects.requireNonNull(id, "id 는 필수 입니다.");
        final Map<EntityId, Object> entityMap = entities.get(entity);
        if (entityMap == null) {
            return null;
        }
        return (T) entityMap.get(id);
    }

    public void put(Class<?> entityClass, EntityId id, Object entity) {
        Objects.requireNonNull(entityClass, "entityClass 는 필수 입니다.");
        Objects.requireNonNull(id, "id 는 필수 입니다.");
        Objects.requireNonNull(entity, "entity 는 필수 입니다.");

        final Map<EntityId, Object> entityMap = entities.computeIfAbsent(entityClass, k -> new HashMap<>());
        entityMap.put(id, entity);
    }

}
