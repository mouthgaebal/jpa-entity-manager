package util;

public final class Preconditions {

    private Preconditions() {}

    public static void checkArgument(boolean condition, String message, Object... args) {
        if (!condition) {
            throw new IllegalArgumentException(message.formatted(args));
        }
    }

    public static void checkState(boolean condition, String message, Object... args) {
        if (!condition) {
            throw new IllegalStateException(message.formatted(args));
        }
    }

}
