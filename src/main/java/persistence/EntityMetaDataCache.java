package persistence;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class EntityMetaDataCache {

    private static final Map<Class<?>, EntityMetaData> ENTITY_META_DATAS_BY_ENTITY_CLASS = new ConcurrentHashMap<>();

    public static EntityMetaData get(Object entity) {
        Objects.requireNonNull(entity, "entity 는 필수 입니다.");
        return get(entity.getClass());
    }

    public static EntityMetaData get(Class<?> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass 는 필수 입니다.");
        return ENTITY_META_DATAS_BY_ENTITY_CLASS.computeIfAbsent(entityClass, EntityMetaData::new);
    }

}
