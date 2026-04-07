package util;

import java.util.Collection;

public final class CollectionUtils {

    private CollectionUtils() {}

    public static boolean isEmpty(Collection<?> collection) {
        if (collection == null) {
            return true;
        }
        return collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }


}
