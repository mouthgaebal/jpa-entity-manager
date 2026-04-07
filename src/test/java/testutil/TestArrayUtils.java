package testutil;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;

public class TestArrayUtils {

    public static <T> Arguments toArguments(T[] array) {
        if (array == null) {
            return Arguments.of(Named.of("null", null));
        }
        return Arguments.of(Named.of(Arrays.toString(array), array));
    }

}
