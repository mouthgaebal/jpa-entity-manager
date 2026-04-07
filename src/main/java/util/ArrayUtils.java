package util;

public final class ArrayUtils {

    private ArrayUtils() {}

    public static boolean isEmpty(Object[] array) {
        if (array == null) {
            return true;
        }
        return array.length == 0;
    }

     public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

}
