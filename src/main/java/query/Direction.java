package query;

import util.Preconditions;
import util.StringUtils;

import java.util.Objects;

public enum Direction {
    ASC,
    DESC,
    ;

    private static final String ERROR_FORMAT = "direction 은 'ASC' 또는 'DESC' 여야 합니다. direction: %s";

    public static Direction from(String direction) {
        Preconditions.checkArgument(StringUtils.isNotBlank(direction), ERROR_FORMAT.formatted(direction));
        final String directionUpper = direction.toUpperCase();
        for (Direction value : Direction.values()) {
            if (Objects.equals(value.name(), directionUpper)) {
                return value;
            }
        }
        throw new IllegalArgumentException(ERROR_FORMAT.formatted(direction));
    }

}
