package persistence;

import annotation.Column;
import annotation.Id;
import annotation.Table;
import jdbc.QueryExecutor;
import query.SelectQueryBuilder;
import util.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityLoader {

    private final QueryExecutor queryExecutor;

    public EntityLoader(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public <T> T load(Class<T> entityClass, Long id) {
        final String tableName = extractTableName(entityClass);
        final String idColumnName = extractIdColumnName(entityClass);

        final String sql = new SelectQueryBuilder()
                .from(tableName)
                .where(idColumnName + " = ?")
                .build();

        return queryExecutor.queryForObject(sql, entityClass, id);
    }

    private String extractTableName(Class<?> entityClass) {
        final Table table = entityClass.getDeclaredAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("entity 는 @Table 이 필수 입니다. entityClass: [%s]".formatted(entityClass.getName()));
        }
        final String tableName = table.name();
        if (StringUtils.isNotBlank(tableName)) {
            return tableName;
        }
        final String simpleName = entityClass.getSimpleName();
        return StringUtils.camelCaseToSnakeCase(simpleName);
    }

    private String extractIdColumnName(Class<?> entityClass) {
        final List<Field> idFields = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .toList();
        validateIdField(idFields, entityClass);
        return extractColumnName(idFields.getFirst());
    }

    private void validateIdField(List<Field> idFields, Class<?> entityClass) {
        if (idFields.size() != 1) {
            final String idFieldNames = idFields.stream()
                    .map(Field::getName)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("entity 는 @Id 가 하나만 있어야 합니다. entityClass: [%s], idFieldNames: %s"
                                                       .formatted(entityClass.getName(), idFieldNames));
        }
    }

    private String extractColumnName(Field field) {
        final Column column = field.getDeclaredAnnotation(Column.class);
        if (column != null) {
            return column.name();
        }
        return StringUtils.camelCaseToSnakeCase(field.getName());
    }

}
