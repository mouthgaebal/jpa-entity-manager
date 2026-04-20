package persistence;

import annotation.Column;
import annotation.Id;
import annotation.Table;
import util.Preconditions;
import util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityMetaData {

    private static final BinaryOperator<Field> COLUMN_MERGE_FUNCTION = (field1, field2) -> {
        throw new IllegalArgumentException("동일한 컬럼명을 가진 필드가 존재합니다. field1: [%s], field2: [%s]"
                                                   .formatted(field1.getName(), field2.getName()));
    };

    private final Class<?> entityClass;
    private final String tableName;
    private final Field idField;
    private final String idColumnName;
    private final LinkedHashMap<String, Field> FieldsByColumnName;

    public EntityMetaData(Class<?> entityClass) {
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass 는 필수 입니다.");
        this.tableName = extractTableName(entityClass);
        this.idField = extractIdField(entityClass);
        this.idColumnName = extractColumnName(idField);
        this.FieldsByColumnName = extractFieldsByColumnName(entityClass);
    }

    private String extractTableName(Class<?> entityClass) {
        final Table table = entityClass.getDeclaredAnnotation(Table.class);
        if (table == null) {
            return StringUtils.camelCaseToSnakeCase(entityClass.getSimpleName());
        }
        final String tableName = table.name();
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException("@Table 의 name 은 필수 입니다. entityClass: [%s]".formatted(entityClass.getName()));
        }
        return tableName;
    }

    private Field extractIdField(Class<?> entityClass) {
        final List<Field> idFields = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .toList();
        validateIdField(idFields);
        return idFields.getFirst();
    }

    private void validateIdField(List<Field> idFields) {
        if (idFields.size() != 1) {
            final String idFieldNames = idFields.stream()
                    .map(Field::getName)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("entity 는 @Id 가 하나만 있어야 합니다. entityClass: [%s], idFieldNames: %s"
                                                       .formatted(entityClass.getName(), idFieldNames));
        }
    }

    private LinkedHashMap<String, Field> extractFieldsByColumnName(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .collect(Collectors.toMap(this::extractColumnName, Function.identity(), COLUMN_MERGE_FUNCTION, LinkedHashMap::new));
    }

    private String extractColumnName(Field field) {
        final Column column = field.getDeclaredAnnotation(Column.class);
        if (column == null) {
            return StringUtils.camelCaseToSnakeCase(field.getName());
        }

        final String columnName = column.name();
        if (StringUtils.isBlank(columnName)) {
            throw new IllegalArgumentException("@Column 의 name 은 필수 입니다. entityClass: [%s], field: [%s]".formatted(entityClass.getName(), field.getName()));
        }

        return columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public Field getIdField() {
        return idField;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public List<String> getColumnNamesExcludeIdColumn() {
        return FieldsByColumnName.keySet().stream()
                .filter(columnName -> !Objects.equals(columnName, idColumnName))
                .toList();
    }

    public List<String> getAllColumnNames() {
        return List.copyOf(FieldsByColumnName.keySet());
    }

    public Optional<Field> getField(String columnName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(columnName), "columnName 은 필수 입니다.");
        return Optional.ofNullable(FieldsByColumnName.get(columnName.toLowerCase()));
    }


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof EntityMetaData metaData)) {
            return false;
        }
        return entityClass.equals(metaData.entityClass);
    }

    @Override
    public int hashCode() {
        return entityClass.hashCode();
    }

}
