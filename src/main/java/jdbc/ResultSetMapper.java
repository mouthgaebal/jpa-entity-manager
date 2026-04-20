package jdbc;

import persistence.EntityMetaData;
import persistence.EntityMetaDataCache;
import util.Preconditions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class ResultSetMapper {

    /**
     * ResultSet의 현재 행을 객체로 변환
     * @param rs ResultSet (이미 next() 호출된 상태)
     * @param targetClass 변환할 클래스
     * @return 변환된 객체
     */
    public <T> T mapToObject(ResultSet rs, Class<T> targetClass) {
        return wrapException(() -> {
            final ResultSetMetaData metaData = rs.getMetaData();
            final EntityMetaData entityMetaData = EntityMetaDataCache.get(targetClass);
            validateFieldTypes(metaData, entityMetaData);
            return createInstance(rs, targetClass, metaData, entityMetaData);
        });
    }

    private void validateFieldTypes(ResultSetMetaData metaData, EntityMetaData entityMetaData) throws Exception {
        final int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            final Field field = getField(metaData, i, entityMetaData);
            if (field == null) {
                continue;
            }

            final int columnType = metaData.getColumnType(i);
            final JDBCType jdbcType = JDBCType.valueOf(columnType);
            validateType(jdbcType, field.getType());
        }
    }

    private <T> T createInstance(ResultSet rs,
                                 Class<T> targetClass,
                                 ResultSetMetaData metaData,
                                 EntityMetaData entityMetaData) throws Exception {

        final Constructor<T> constructor = targetClass.getDeclaredConstructor();
        final T instance = constructor.newInstance();
        final int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            final Field field = getField(metaData, i, entityMetaData);
            if (field == null) {
                continue;
            }
            final Object value = getColumnValue(rs, i, field);
            field.setAccessible(true);
            field.set(instance, value);
        }
        return instance;
    }

    private Field getField(ResultSetMetaData metaData, int columnIndex, EntityMetaData entityMetaData) throws Exception {
        final String columnName = metaData.getColumnName(columnIndex).toLowerCase();
        return entityMetaData.getField(columnName)
                .orElse(null);
    }

    private Object getColumnValue(ResultSet rs, int index, Field field) throws Exception {
        final Class<?> type = field.getType();
        if (type.isPrimitive()) {
            return getPrimitiveTypeValue(rs, index, type);
        }
        return rs.getObject(index, type);
    }

    private Object getPrimitiveTypeValue(ResultSet rs, int index, Class<?> type) throws Exception {
        return PrimitiveTypeMapper.mapPrimitiveType(rs, index, type);
    }

    private void validateType(JDBCType jdbcType, Class<?> type) {
        if (!JdbcTypeValidator.isAllowedType(jdbcType, type)) {
            throw new IllegalArgumentException("지원하지 않는 JDBC 타입입니다. jdbcType: [%s], fieldType: [%s]".formatted(jdbcType, type.getName()));
        }
    }

    /**
     * ResultSet의 모든 행을 리스트로 변환
     * @param rs ResultSet
     * @param targetClass 변환할 클래스
     * @return 변환된 객체 리스트
     */
    public <T> List<T> mapToList(ResultSet rs, Class<T> targetClass) {
        return wrapException(() -> {
            final List<T> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapToObject(rs, targetClass));
            }
            return result;
        });
    }

    private <T> T wrapException(ExceptionSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private interface ExceptionSupplier<T> {
        T get() throws Exception;
    }

    static class PrimitiveTypeMapper {

        private static final Map<Class<?>, BiFunctionWithException<ResultSet, Integer, Object>> PRIMITIVE_TYPE_MAPPERS = Map.ofEntries(
                entry(boolean.class, ResultSet::getBoolean),
                entry(byte.class, ResultSet::getByte),
                entry(short.class, ResultSet::getShort),
                entry(int.class, ResultSet::getInt),
                entry(long.class, ResultSet::getLong),
                entry(float.class, ResultSet::getFloat),
                entry(double.class, ResultSet::getDouble)
        );

        public static Object mapPrimitiveType(ResultSet rs, int index, Class<?> type) throws Exception {
            Preconditions.checkArgument(type.isPrimitive(), "type 은 primitive 타입이어야 합니다. type: [%s]".formatted(type.getName()));
            final BiFunctionWithException<ResultSet, Integer, Object> mapper = PRIMITIVE_TYPE_MAPPERS.get(type);
            if (mapper == null) {
                throw new IllegalArgumentException("지원하지 않는 primitive 타입입니다. type: [%s]".formatted(type.getName()));
            }
            return mapper.apply(rs, index);
        }

    }

}
