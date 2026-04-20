package persistence;

import jdbc.QueryExecutor;
import query.SelectQueryBuilder;

import java.sql.Connection;

public class EntityLoader {

    private final QueryExecutor queryExecutor;

    public EntityLoader(Connection connection) {
        this(new QueryExecutor(connection));
    }

    public EntityLoader(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public <T> T load(Class<T> entityClass, Long id) {
        final EntityMetaData entityMetaData = EntityMetaDataCache.get(entityClass);
        final String tableName = entityMetaData.getTableName();
        final String idColumnName = entityMetaData.getIdColumnName();

        final String sql = new SelectQueryBuilder()
                .from(tableName)
                .where(idColumnName + " = ?")
                .build();

        return queryExecutor.queryForObject(sql, entityClass, id);
    }

}
