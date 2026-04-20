package util;

import java.lang.reflect.Field;

public class ReflectionUtils {

        public static Object getValue(Field field, Object target) {
            try {
                field.setAccessible(true);
                return field.get(target);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

}
