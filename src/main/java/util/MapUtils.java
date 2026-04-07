package util;

import java.util.Map;

public final class MapUtils {

    private MapUtils() {}

    public static boolean isEmpty(Map<?, ?> map) {
        if (map == null) {
            return true;
        }
        return map.isEmpty();
    }

        public static boolean isNotEmpty(Map<?, ?> map) {
            return !isEmpty(map);
        }

}
