package jdbc;

import util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            final Map<String, Field> fieldsByColumnName = Arrays.stream(targetClass.getDeclaredFields())
                    .collect(Collectors.toUnmodifiableMap(this::toLowerCaseColumnName, Function.identity()));

            validateFieldTypes(metaData, fieldsByColumnName);
            return createInstance(rs, targetClass, metaData, fieldsByColumnName);
        });
    }

    private String toLowerCaseColumnName(Field field) {
        return StringUtils.camelCaseToSnakeCase(field.getName());
    }

    private void validateFieldTypes(ResultSetMetaData metaData, Map<String, Field> fieldsByColumnName) throws SQLException {
        final int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            final Field field = getField(metaData, i, fieldsByColumnName);
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
                                 Map<String, Field> fieldsByColumnName) throws Exception {

        final Constructor<T> constructor = targetClass.getDeclaredConstructor();
        final T instance = constructor.newInstance();
        final int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            final Field field = getField(metaData, i, fieldsByColumnName);
            if (field == null) {
                continue;
            }
            final Object value = getColumnValue(rs, i, field);
            field.setAccessible(true);
            field.set(instance, value);
        }
        return instance;
    }

    private Object getColumnValue(ResultSet rs, int index, Field field) throws SQLException {
        final Class<?> type = field.getType();
        if (type.isPrimitive()) {
            return getPrimitiveTypeValue(rs, index, type);
        }
        return rs.getObject(index, type);
    }

    private Object getPrimitiveTypeValue(ResultSet rs, int index, Class<?> type) throws SQLException {
        if (type == boolean.class) {
            return rs.getBoolean(index);
        }
        if (type == byte.class) {
            return rs.getByte(index);
        }
        if (type == short.class) {
            return rs.getShort(index);
        }
        if (type == int.class) {
            return rs.getInt(index);
        }
        if (type == long.class) {
            return rs.getLong(index);
        }
        if (type == float.class) {
            return rs.getFloat(index);
        }
        if (type == double.class) {
            return rs.getDouble(index);
        }
        throw new IllegalArgumentException("지원하지 않는 primitive 타입입니다. type: [%s]".formatted(type.getName()));
    }

    private Field getField(ResultSetMetaData metaData, int columnIndex, Map<String, Field> fieldsByColumnName) throws SQLException {
        final String columnName = metaData.getColumnName(columnIndex).toLowerCase();
        return fieldsByColumnName.get(columnName);
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

}
