package persistence;

import jdbc.GeneratedKey;
import jdbc.QueryExecutor;
import query.InsertQueryBuilder;
import util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class EntityPersister {

    private final QueryExecutor queryExecutor;

    public EntityPersister(Connection connection) {
        this(new QueryExecutor(connection));
    }

    public EntityPersister(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public Object insert(Object target) {
        final Class<?> targetClass = target.getClass();
        final EntityMetaData entityMetaData = EntityMetaDataCache.get(targetClass);

        final Field idField = entityMetaData.getIdField();
        final Object idValue = ReflectionUtils.getValue(idField, target);
        if (idValue == null) {
            return executeGeneratedValueInsertSql(target, entityMetaData);
        } else {
            return executeInsertSql(target, entityMetaData, idValue);
        }
    }

    private Number executeGeneratedValueInsertSql(Object target, EntityMetaData entityMetaData) {
        final List<String> columnNamesExcludeIdColumn = entityMetaData.getColumnNamesExcludeIdColumn();
        final String sql = createInsertSql(entityMetaData.getTableName(), columnNamesExcludeIdColumn);
        final Object[] columnValues = getColumnValues(target, columnNamesExcludeIdColumn, entityMetaData);
        final GeneratedKey keyHolder = new GeneratedKey(entityMetaData.getIdColumnName());
        queryExecutor.execute(sql, keyHolder, columnValues);
        return keyHolder.getKey();
    }

    private Object executeInsertSql(Object target, EntityMetaData entityMetaData, Object idValue) {
        final List<String> allColumnNames = entityMetaData.getAllColumnNames();
        final String sql = createInsertSql(entityMetaData.getTableName(), allColumnNames);
        final Object[] columnValues = getColumnValues(target, allColumnNames, entityMetaData);
        queryExecutor.execute(sql, columnValues);
        return idValue;
    }

    private String createInsertSql(String tableName, List<String> columnNames) {
        final InsertQueryBuilder builder = new InsertQueryBuilder()
                .into(tableName);

        for (String columnName : columnNames) {
            builder.value(columnName, "?");
        }
        return builder.build();
    }

    private Object [] getColumnValues(Object target, List<String> columnNamesExcludeIdColumn, EntityMetaData entityMetaData) {
        return columnNamesExcludeIdColumn.stream()
                .map(entityMetaData::getField)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(field -> ReflectionUtils.getValue(field, target))
                .toArray(Object[]::new);
    }

}
