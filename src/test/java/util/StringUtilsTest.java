package util;

import annotation.BlankSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import testutil.TestArrayUtils;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringUtilsTest {

    @ParameterizedTest(name = "isBlank - 입력값이 공백문자이거나 null 인경우 true. input: [{0}]")
    @BlankSource
    void isBlank(String input) throws Exception {
        assertThat(StringUtils.isBlank(input)).isTrue();
    }

    @DisplayName("isBlank - 입력값이 공백문자가 아닌 경우 false")
    @Test
    void isBlank1() throws Exception {
        final String input = "a";
        assertThat(StringUtils.isBlank(input)).isFalse();
    }

    @ParameterizedTest(name = "isNotBlank - 입력값이 공백문자이거나 null 인경우 false. input: [{0}]")
    @BlankSource
    void isNotBlank(String input) throws Exception {
        assertThat(StringUtils.isNotBlank(input)).isFalse();
    }

    @DisplayName("isNotBlank - 입력값이 공백문자가 아닌 경우 true")
    @Test
    void isNotBlank1() throws Exception {
        final String input = "a";
        assertThat(StringUtils.isNotBlank(input)).isTrue();
    }

    @ParameterizedTest(name = "anyBlank - 입력값 중 하나라도 공백문자이거나 null 인경우 true. input: {0}")
    @MethodSource("anyBlank")
    void anyBlank(String[] input) throws Exception {
        assertThat(StringUtils.anyBlank(input)).isTrue();
    }

    private static Stream<Arguments> anyBlank() {
        return Stream.of(
                TestArrayUtils.toArguments(new String[]{null}),
                TestArrayUtils.toArguments(new String[]{""}),
                TestArrayUtils.toArguments(new String[]{" "}),
                TestArrayUtils.toArguments(new String[]{null, null}),
                TestArrayUtils.toArguments(new String[]{"", ""}),
                TestArrayUtils.toArguments(new String[]{" ", " "}),
                TestArrayUtils.toArguments(new String[]{null, ""}),
                TestArrayUtils.toArguments(new String[]{null, " "}),
                TestArrayUtils.toArguments(new String[]{"", null}),
                TestArrayUtils.toArguments(new String[]{" ", null}),
                TestArrayUtils.toArguments(new String[]{"a", null}),
                TestArrayUtils.toArguments(new String[]{"a", ""}),
                TestArrayUtils.toArguments(new String[]{"a", " "}),
                TestArrayUtils.toArguments(new String[]{null, "a"}),
                TestArrayUtils.toArguments(new String[]{"", "a"}),
                TestArrayUtils.toArguments(new String[]{" ", "a"})
        );
    }


    @ParameterizedTest(name = "anyBlank - 입력값이 모두 공백문자가 아닌 경우 false. input: {0}")
    @MethodSource("anyBlank1")
    void anyBlank1(String[] input) throws Exception {
        assertThat(StringUtils.anyBlank(input)).isFalse();
    }

    private static Stream<Arguments> anyBlank1() {
        return Stream.of(
                TestArrayUtils.toArguments(new String[]{"a"}),
                TestArrayUtils.toArguments(new String[]{"a", "b"})
        );
    }

    @ParameterizedTest(name = "allNotBlank - 입력값 중 하나라도 공백문자이거나 null 인경우 false. input: {0}")
    @MethodSource("allNotBlank")
    void allNotBlank(String[] input) throws Exception {
        assertThat(StringUtils.allNotBlank(input)).isFalse();
    }

    private static Stream<Arguments> allNotBlank() {
        return Stream.of(
                TestArrayUtils.toArguments(new String[]{null}),
                TestArrayUtils.toArguments(new String[]{""}),
                TestArrayUtils.toArguments(new String[]{" "}),
                TestArrayUtils.toArguments(new String[]{null, null}),
                TestArrayUtils.toArguments(new String[]{"", ""}),
                TestArrayUtils.toArguments(new String[]{" ", " "}),
                TestArrayUtils.toArguments(new String[]{null, ""}),
                TestArrayUtils.toArguments(new String[]{null, " "}),
                TestArrayUtils.toArguments(new String[]{"", null}),
                TestArrayUtils.toArguments(new String[]{" ", null}),
                TestArrayUtils.toArguments(new String[]{"a", null}),
                TestArrayUtils.toArguments(new String[]{"a", ""}),
                TestArrayUtils.toArguments(new String[]{"a", " "}),
                TestArrayUtils.toArguments(new String[]{null, "a"}),
                TestArrayUtils.toArguments(new String[]{"", "a"}),
                TestArrayUtils.toArguments(new String[]{" ", "a"})
        );
    }

    @DisplayName("allNotBlank - 입력값이 모두 공백문자가 아닌 경우 true")
    @Test
    void allNotBlank1() throws Exception {
        final String[] input = new String[] {"a"};
        assertThat(StringUtils.allNotBlank(input)).isTrue();
    }

    @ParameterizedTest(name = "camelCaseToSnakeCase - 입력값이 null 이거나 빈 문자열인 경우 에러. input: [{0}]")
    @BlankSource
    void camelCaseToSnakeCase(String str) throws Exception {
        assertThatThrownBy(() -> StringUtils.camelCaseToSnakeCase(str))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("str 은 null 또는 빈 문자열일 수 없습니다.");
    }

    @DisplayName("camelCaseToSnakeCase - 입력값이 첫번째 글자가 대문자인 경우 에러")
    @Test
    void camelCaseToSnakeCase1() throws Exception {
        // given
        final String str = "CamelCase";

        // when
        assertThatThrownBy(() -> StringUtils.camelCaseToSnakeCase(str))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("camelCase 는 첫 글자가 소문자여야 합니다. str: [%s]", str);

    }

    @ParameterizedTest(name = "camelCaseToSnakeCase - 입력값에 특수문자가 포함된경우 에러. input: [{0}]")
    @MethodSource("camelCaseToSnakeCase2")
    void camelCaseToSnakeCase2(String str) throws Exception {
        assertThatThrownBy(() -> StringUtils.camelCaseToSnakeCase(str))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("camelCase 는 영문자와 숫자로만 구성되어야 합니다. str: [%s]", str);
    }

    private static Stream<Arguments> camelCaseToSnakeCase2() {
        return Stream.of(
                Arguments.of("_camelCase"),
                Arguments.of("camel-case"),
                Arguments.of("camel case"),
                Arguments.of("camelCase!"),
                Arguments.of("camel_case")
        );
    }

}
