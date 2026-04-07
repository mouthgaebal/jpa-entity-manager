package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionUtilsTest {

    @ParameterizedTest(name = "isEmpty - 입력값이 null 이거나 길이가 0인 경우 true. input: {0}")
    @MethodSource("isEmpty")
    void isEmpty(Collection<?> input) throws Exception {
        assertThat(CollectionUtils.isEmpty(input)).isTrue();
    }

    private static Collection<?>[] isEmpty() {
        return new Collection<?>[] {
                null,
                List.of()
        };
    }

    @DisplayName("isEmpty - 입력값의 길이가 0이 아닌 경우 false")
    @Test
    void isEmpty1() throws Exception {
        final Collection<?> input = List.of("abc");
        assertThat(CollectionUtils.isEmpty(input)).isFalse();
    }

    @ParameterizedTest(name = "isNotEmpty - 입력값이 null 이거나 길이가 0인 경우 false. input: {0}")
    @MethodSource("isNotEmpty")
    void isNotEmpty(Collection<?> input) throws Exception {
        assertThat(CollectionUtils.isNotEmpty(input)).isFalse();
    }

    private static Collection<?>[] isNotEmpty() {
        return new Collection<?>[] {
                null,
                List.of()
        };
    }

    @DisplayName("isNotEmpty - 입력값의 길이가 0이 아닌 경우 true")
    @Test
    void isNotEmpty1() throws Exception {
        final Collection<?> input = List.of("abc");
        assertThat(CollectionUtils.isNotEmpty(input)).isTrue();
    }

}