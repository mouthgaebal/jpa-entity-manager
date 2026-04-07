package jdbc;

import query.NamedParameterQuery;
import util.Preconditions;
import util.StringUtils;

import java.sql.*;
import java.util.List;
import java.util.Objects;

public class QueryExecutor {
    private final Connection connection;
    private final ResultSetMapper mapper;

    public QueryExecutor(Connection connection) {
        this.connection = connection;
        this.mapper = new ResultSetMapper();
    }

    public <T> T queryForObject(String sql, Class<T> resultClass, Object... params) {
        final List<T> results = query(sql, resultClass, params);
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new IllegalStateException("결과가 2개 이상입니다. sql: [%s]".formatted(sql));
        }
        return results.getFirst();
    }

    /**
     * SELECT 쿼리 실행 및 자동 매핑
     */
    public <T> List<T> query(String sql, Class<T> resultClass, Object... params) {
        Preconditions.checkArgument(StringUtils.isNotBlank(sql), "sql 은 필수 입니다.");
        Objects.requireNonNull(resultClass, "resultClass 는 필수 입니다.");
        Objects.requireNonNull(params, "params 는 null 일 수 없습니다.");

        return executePrepareStatement(sql, pstmt -> {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            final ResultSet rs = pstmt.executeQuery();
            return mapper.mapToList(rs, resultClass);
        });
    }

    /**
     * Named Parameter 쿼리 실행
     */
    public <T> List<T> query(NamedParameterQuery query, Class<T> resultClass) {
        Objects.requireNonNull(query, "query 는 필수 입니다.");
        Objects.requireNonNull(resultClass, "resultClass 는 필수 입니다.");

        final String sql = query.toJdbcSql();
        return executePrepareStatement(sql, pstmt -> {
            query.bindParameters(pstmt);
            final ResultSet rs = pstmt.executeQuery();
            return mapper.mapToList(rs, resultClass);
        });
    }

    /**
     * INSERT/UPDATE/DELETE 실행
     */
    public int execute(String sql, Object... params) {
        Preconditions.checkArgument(StringUtils.isNotBlank(sql), "sql 은 필수 입니다.");
        Objects.requireNonNull(params, "params 는 null 일 수 없습니다.");
        return executePrepareStatement(sql, pstmt -> {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeUpdate();
        });
    }

    /**
     * Named Parameter로 실행
     */
    public int execute(NamedParameterQuery query) {
        Objects.requireNonNull(query, "query 는 필수 입니다.");
        final String sql = query.toJdbcSql();
        return executePrepareStatement(sql, pstmt -> {
            query.bindParameters(pstmt);
            return pstmt.executeUpdate();
        });
    }

    private <T> T executePrepareStatement(String sql, FunctionWithException<PreparedStatement, T> function) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            return function.apply(pstmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void executeQuery(String sql) {
        Preconditions.checkArgument(StringUtils.isNotBlank(sql), "sql 은 필수 입니다.");
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
