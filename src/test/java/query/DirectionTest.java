package query;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DirectionTest {

    @ParameterizedTest(name = "from - 입력값이 ASC, DESC 인 경우 Direction 객체 반환. input: [{0}], expected: [{1}]")
    @MethodSource("from")
    void from(String direction, Direction expected) throws Exception {
        assertThat(Direction.from(direction)).isEqualTo(expected);
    }

    private static Object[][] from() {
        return new Object[][] {
                { "ASC", Direction.ASC },
                { "asc", Direction.ASC },
                { "DESC", Direction.DESC },
                { "desc", Direction.DESC }
        };
    }

    @ParameterizedTest(name = "from - 입력값이 ASC, DESC 가 아닌경우 에러. input: [{0}]")
    @MethodSource("from1")
    void from1(String direction) throws Exception {
        assertThatThrownBy(() -> Direction.from(direction))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("direction 은 'ASC' 또는 'DESC' 여야 합니다.");
    }

    private static Object[][] from1() {
        return new Object[][] {
                { null },
                { "" },
                { " " },
                { "a" }
        };
    }

}