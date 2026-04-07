package util;

public final class StringUtils {

    private StringUtils() {}

    public static boolean allNotBlank(String... strings) {
        return !anyBlank(strings);
    }

    public static boolean anyBlank(String... strings) {
        if (strings == null) {
            return true;
        }

        for (String str : strings) {
            if (isBlank(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        return str.isBlank();
    }

    public static String camelCaseToSnakeCase(String str) {
        validateCamelCase(str);
        final char[] chars = str.toCharArray();
        final StringBuilder sb = new StringBuilder();
        for (final char ch : chars) {
            if (Character.isUpperCase(ch)) {
                sb.append('_').append(Character.toLowerCase(ch));
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    private static void validateCamelCase(String str) {
        Preconditions.checkArgument(isNotBlank(str), "str 은 null 또는 빈 문자열일 수 없습니다.");
        final char[] chars = str.toCharArray();
        if (Character.isUpperCase(chars[0])) {
            throw new IllegalArgumentException("camelCase 는 첫 글자가 소문자여야 합니다. str: [%s]".formatted(new String(chars)));
        }
        for (char ch : chars) {
            if (!Character.isLetterOrDigit(ch)) {
                throw new IllegalArgumentException("camelCase 는 영문자와 숫자로만 구성되어야 합니다. str: [%s]".formatted(new String(chars)));
            }
        }
    }

}
