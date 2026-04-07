package query;

import annotation.BlankSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteQueryBuilderTest {

    @ParameterizedTest(name = "from - 테이블 이름이 비어있으면 에러. table: [{0}]")
    @BlankSource
    void from(String table) throws Exception {
        assertThatThrownBy(() -> new DeleteQueryBuilder().from(table))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("테이블 이름은 비어있을 수 없습니다.");
    }

    @ParameterizedTest(name = "where - WHERE 조건이 비어있으면 에러. condition: [{0}]")
    @BlankSource
    void where(String condition) throws Exception {
        assertThatThrownBy(() -> new DeleteQueryBuilder().where(condition))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WHERE 조건은 비어있을 수 없습니다.");
    }

    @DisplayName("build - 테이블 이름이 지정되지 않으면 에러")
    @Test
    void build() throws Exception {
        assertThatThrownBy(() -> new DeleteQueryBuilder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("테이블 이름이 지정되지 않았습니다.");
    }

    @Test
    void build1() throws Exception {
        final String sql = new DeleteQueryBuilder()
                .from("users")
                .where("id = ?")
                .build();

        final String expected = """
                DELETE FROM users
                WHERE id = ?
                """.stripTrailing();

        assertThat(sql).isEqualTo(expected);
    }

    @DisplayName("build - WHERE 절이 없는 경우")
    @Test
    void build2() throws Exception {
        final String sql = new DeleteQueryBuilder()
                .from("users")
                .build();

        final String expected = "DELETE FROM users";

        assertThat(sql).isEqualTo(expected);
    }

}