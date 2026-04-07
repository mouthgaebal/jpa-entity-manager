package jdbc;

import query.NamedParameterQuery;
import util.Preconditions;
import util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class QueryExecutor {
    private final Connection connection;
    private final ResultSetMapper mapper;

    public QueryExecutor(Connection connection) {
        this.connection = connection;
        this.mapper = new ResultSetMapper();
    }

    /**
     * SELECT 쿼리 실행 및 자동 매핑
     */
    public <T> List<T> query(String sql, Class<T> resultClass, Object... params) throws SQLException {
        Preconditions.checkArgument(StringUtils.isNotBlank(sql), "sql 은 필수 입니다.");
        Objects.requireNonNull(resultClass, "resultClass 는 필수 입니다.");
        Objects.requireNonNull(params, "params 는 null 일 수 없습니다.");

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            final ResultSet rs = statement.executeQuery();
            return mapper.mapToList(rs, resultClass);
        }
    }

    /**
     * Named Parameter 쿼리 실행
     */
    public <T> List<T> query(NamedParameterQuery query, Class<T> resultClass) throws SQLException {
        Objects.requireNonNull(query, "query 는 필수 입니다.");
        Objects.requireNonNull(resultClass, "resultClass 는 필수 입니다.");

        final String sql = query.toJdbcSql();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            query.bindParameters(statement);
            final ResultSet rs = statement.executeQuery();
            return mapper.mapToList(rs, resultClass);
        }
    }

    /**
     * INSERT/UPDATE/DELETE 실행
     */
    public int execute(String sql, Object... params) throws SQLException {
        Preconditions.checkArgument(StringUtils.isNotBlank(sql), "sql 은 필수 입니다.");
        Objects.requireNonNull(params, "params 는 null 일 수 없습니다.");
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return statement.executeUpdate();
        }
    }

    /**
     * Named Parameter로 실행
     */
    public int execute(NamedParameterQuery query) throws SQLException {
        Objects.requireNonNull(query, "query 는 필수 입니다.");
        final String sql = query.toJdbcSql();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            query.bindParameters(statement);
            return statement.executeUpdate();
        }
    }
}
