package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MapUtilsTest {

    @ParameterizedTest(name = "isEmpty - map 이 null 이거나 비어있으면 true 를 반환한다. map: {0}")
    @MethodSource("isEmpty")
    void isEmpty(Map<?, ?> map) throws Exception {
        assertThat(MapUtils.isEmpty(map)).isTrue();
    }

    private static Stream<Arguments> isEmpty() {
        return Stream.of(
                null,
                Arguments.of(Map.of())
        );
    }

    @DisplayName("isEmpty - map 이 null 이 아니고 비어있지 않으면 false 를 반환한다.")
    @Test
    void isEmtpy1() {
        // given
        final Map<String, String> map = Map.of("key", "value");

        // when
        assertThat(MapUtils.isEmpty(map)).isFalse();
    }

    @ParameterizedTest(name = "isNotEmpty - map 이 null 이거나 비어있으면 false 를 반환한다. map: {0}")
    @MethodSource("isNotEmpty")
    void isNotEmpty(Map<?, ?> map) throws Exception {
        assertThat(MapUtils.isNotEmpty(map)).isFalse();
    }

    private static Stream<Arguments> isNotEmpty() {
        return Stream.of(
                null,
                Arguments.of(Map.of())
        );
    }

    @DisplayName("isNotEmpty - map 이 null 이 아니고 비어있지 않으면 true 를 반환한다.")
    @Test
    void isNotEmpty1() {
        // given
        final Map<String, String> map = Map.of("key", "value");

        // when
        assertThat(MapUtils.isNotEmpty(map)).isTrue();
    }

}