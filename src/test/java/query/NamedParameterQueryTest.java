package query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestPreparedStatementAdapter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class NamedParameterQueryTest {

    @DisplayName("sql 에 중복된 파라미터명이 존재하는 경우 에러")
    @Test
    void create() throws Exception {
        // given
        final String sql = "SELECT * FROM users WHERE name = :name OR email = :name";

        // when
        assertThatThrownBy(() -> new NamedParameterQuery(sql))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 파라미터 이름이 있습니다. parameterName: [name]");
    }

    @Test
    void toJdbcSql() throws Exception {
        // given
        final String sql = "INSERT INTO users (name, age, email) VALUES (:name, :age, :email)";

        final NamedParameterQuery namedParameterQuery = new NamedParameterQuery(sql);

        // when
        final String result = namedParameterQuery.toJdbcSql();

        // then
        assertThat(result).isEqualTo("INSERT INTO users (name, age, email) VALUES (?, ?, ?)");
    }

    @Test
    void bindParameters() throws Exception {
        // given
        final String sql = "INSERT INTO users (name, age, email) VALUES (:name, :age, :email)";

        // when
        final NamedParameterQuery namedParameterQuery = new NamedParameterQuery(sql)
                .setParameter("age", 31)
                .setParameter("email", "name1@example.com")
                .setParameter("name", "name1");

        final MockPreparedStatement pstmt = new MockPreparedStatement();

        // then
        namedParameterQuery.bindParameters(pstmt);

        final Map<Integer, Object> parametersByIndex = pstmt.getParametersByIndex();
        assertThat(parametersByIndex).hasSize(3);
        assertSoftly(assertions -> {
            assertions.assertThat(parametersByIndex.get(1)).isEqualTo("name1");
            assertions.assertThat(parametersByIndex.get(2)).isEqualTo(31);
            assertions.assertThat(parametersByIndex.get(3)).isEqualTo("name1@example.com");
        });
    }

    @DisplayName("bindParameters - 설정되지 않은 파라미터가 존재하는 경우 에러")
    @Test
    void bindParameters1() throws Exception {
        // given
        final String sql = "INSERT INTO users (name, age, email) VALUES (:name, :age, :email)";

        // when
        final NamedParameterQuery namedParameterQuery = new NamedParameterQuery(sql)
                .setParameter("age", 31)
                .setParameter("name", "name1");

        final MockPreparedStatement pstmt = new MockPreparedStatement();

        // then
        assertThatThrownBy(()     -> namedParameterQuery.bindParameters(pstmt))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("값이 설정되지 않은 파라미터가 있습니다. parameterNames: [email]");
    }

    private static class MockPreparedStatement extends TestPreparedStatementAdapter {

        private Map<Integer, Object> parametersByIndex = new HashMap<>();

        @Override
        public void setObject(int parameterIndex, Object x) throws SQLException {
            parametersByIndex.put(parameterIndex, x);
        }

        public Map<Integer, Object> getParametersByIndex() {
            return parametersByIndex;
        }

    }

}