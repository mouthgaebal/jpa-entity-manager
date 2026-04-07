package query;

import annotation.BlankSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InsertQueryBuilderTest {

    @ParameterizedTest(name = "into - 테이블 이름이 비어있으면 에러. table: [{0}]")
    @BlankSource
    void into(String table) throws Exception {
        assertThatThrownBy(() -> new InsertQueryBuilder().into(table))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("테이블 이름은 비어있을 수 없습니다.");
    }

    @ParameterizedTest(name = "value - 컬럼명 또는 값이 비어있으면 에러. column: [{0}], value: [{1}]")
    @MethodSource("value")
    void value(String column, String value) throws Exception {
        assertThatThrownBy(() -> new InsertQueryBuilder().value(column, value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("컬럼명과 값은 비어있을 수 없습니다. column: [%s], value: [%s]", column, value);
    }

    private static Stream<Arguments> value() {
        return Stream.of(
                Arguments.of(null, "?"),
                Arguments.of("", "?"),
                Arguments.of("name", null),
                Arguments.of("name", ""),
                Arguments.of("", ""),
                Arguments.of(null, null)

        );
    }

    @ParameterizedTest(name = "values - columnValues 가 null 이거나 비어있으면 에러. columnValues: [{0}]")
    @MethodSource("values")
    void values(Map<String, String> columnValues) throws Exception {
        assertThatThrownBy(() -> new InsertQueryBuilder().values(columnValues))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("columnValues는 null 이거나 비어있을 수 없습니다.");
    }

    private static Stream<Arguments> values() {
        return Stream.of(
                Arguments.of((Map<String, String>) null),
                Arguments.of(Map.of())
        );
    }

    @Test
    void build() {
        // given
        final InsertQueryBuilder builder = new InsertQueryBuilder()
                .into("users")
                .value("id", "?")
                .value("name", "?")
                .value("email", "?");

        // when
        final String sql = builder.build();

        // then
        final String expectedSql = """
                INSERT INTO users
                 (id, name, email)
                 VALUES (?, ?, ?)
                """.stripTrailing();
        assertThat(sql).isEqualTo(expectedSql);
    }

    @Test
    void build1() throws Exception {
        // given
        final Map<String, String> columnValues = new LinkedHashMap<>();
        columnValues.put("id", "?");
        columnValues.put("name", "?");
        columnValues.put("price", "?");

        final InsertQueryBuilder builder = new InsertQueryBuilder()
                .into("products")
                .values(columnValues);

        // when
        final String sql = builder.build();

        // then
        final String expectedSql = """
                INSERT INTO products
                 (id, name, price)
                 VALUES (?, ?, ?)
                """.stripTrailing();
        assertThat(sql).isEqualTo(expectedSql);
    }

    @DisplayName("build - 테이블 이름이 지정되지 않으면 에러")
    @Test
    void build2() throws Exception {
        // given
        final InsertQueryBuilder builder = new InsertQueryBuilder()
                .value("id", "?");

        // when
        assertThatThrownBy(() -> builder.build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("테이블 이름이 지정되지 않았습니다.");
    }

    @DisplayName("build - 컬럼-값 쌍이 지정되지 않으면 에러")
    @Test
    void build3() throws Exception {
        // given
        final InsertQueryBuilder builder = new InsertQueryBuilder()
                .into("users");

        // when
        assertThatThrownBy(() -> builder.build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("컬럼-값 쌍이 지정되지 않았습니다.");
    }

}