package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class ArrayUtilsTest {

    @ParameterizedTest(name = "isEmpty - 입력값이 null 이거나 길이가 0인 경우 true. input: {0}")
    @MethodSource("isEmpty")
    void isEmpty(Object[] input) throws Exception {
        assertThat(ArrayUtils.isEmpty(input)).isTrue();
    }

    private static Object[][] isEmpty() {
        return new Object[][] {
                { null },
                { new Object[] {} }
        };
    }

    @DisplayName("isEmpty - 입력값의 길이가 0이 아닌 경우 false")
    @Test
    void isEmpty1() throws Exception {
        final Object[] input = new Object[] { "abc" };
        assertThat(ArrayUtils.isEmpty(input)).isFalse();
    }

    @ParameterizedTest(name = "isNotEmpty - 입력값이 null 이거나 길이가 0인 경우 false. input: {0}")
    @MethodSource("isNotEmpty")
    void isNotEmpty(Object[] input) throws Exception {
        assertThat(ArrayUtils.isNotEmpty(input)).isFalse();
    }

    private static Object[][] isNotEmpty() {
        return new Object[][] {
                { null },
                { new Object[] {} }
        };
    }

    @DisplayName("isNotEmpty - 입력값의 길이가 0이 아닌 경우 true")
    @Test
    void isNotEmpty1() throws Exception {
        final Object[] input = new Object[] { "abc" };
        assertThat(ArrayUtils.isNotEmpty(input)).isTrue();
    }

}