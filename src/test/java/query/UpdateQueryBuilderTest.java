package query;

import annotation.BlankSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateQueryBuilderTest {

    @ParameterizedTest(name = "table - 테이블 이름이 비어있으면 에러. table: [{0}]")
    @BlankSource
    void table(String table) throws Exception {
        assertThatThrownBy(() -> new UpdateQueryBuilder().table(table))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("테이블 이름은 비어있을 수 없습니다.");
    }

    @ParameterizedTest(name = "set - 컬럼명 또는 값이 비어있으면 에러. column: [{0}], value: [{1}]")
    @MethodSource("set")
    void set(String column, String value) throws Exception {
        assertThatThrownBy(() -> new UpdateQueryBuilder().set(column, value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("컬럼명과 값은 비어있을 수 없습니다. column: [%s], value: [%s]", column, value);
    }

    private static Stream<Arguments> set() {
        return Stream.of(
                Arguments.of(null, "?"),
                Arguments.of("", "?"),
                Arguments.of("name", null),
                Arguments.of("name", ""),
                Arguments.of("", ""),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest(name = "where - WHERE 조건이 비어있으면 에러. condition: [{0}]")
    @BlankSource
    void where(String condition) throws Exception {
        assertThatThrownBy(() -> new UpdateQueryBuilder().where(condition))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WHERE 조건은 비어있을 수 없습니다.");
    }

    @Test
    void build() throws Exception {
        final String sql = new UpdateQueryBuilder()
                .table("users")
                .set("name", "?")
                .set("age", "?")
                .where("id = ?")
                .build();

        final String expected = """
               UPDATE users
               SET name = ?, age = ?
               WHERE id = ?
               """.stripTrailing();

        assertThat(sql).isEqualTo(expected);
    }

    @DisplayName("build - where 절이 없는 경우")
    @Test
    void build1() throws Exception {
        final String sql = new UpdateQueryBuilder()
                .table("users")
                .set("name", "?")
                .set("age", "?")
                .build();

        final String expected = """
               UPDATE users
               SET name = ?, age = ?
               """.stripTrailing();

        assertThat(sql).isEqualTo(expected);
    }

}