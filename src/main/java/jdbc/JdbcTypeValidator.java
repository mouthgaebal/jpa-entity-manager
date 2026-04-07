package jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JdbcTypeValidator {

    private static final Map<JDBCType, Set<Class<?>>> ALLOWED_TYPES = new EnumMap<>(JDBCType.class);

    static {
        ALLOWED_TYPES.put(JDBCType.BIT, Set.of(Boolean.class, boolean.class, Byte.class, byte.class, Short.class, short.class, Integer.class, int.class, Long.class, long.class, BigInteger.class));
        ALLOWED_TYPES.put(JDBCType.BOOLEAN, Set.of(Boolean.class, boolean.class));
        ALLOWED_TYPES.put(JDBCType.TINYINT, Set.of(Byte.class, byte.class, Short.class, short.class, Integer.class, int.class, Long.class, long.class, BigInteger.class));
        ALLOWED_TYPES.put(JDBCType.SMALLINT, Set.of(Short.class, short.class, Integer.class, int.class, Long.class, long.class, BigInteger.class));
        ALLOWED_TYPES.put(JDBCType.INTEGER, Set.of(Integer.class, int.class, Long.class, long.class, BigInteger.class));
        ALLOWED_TYPES.put(JDBCType.BIGINT, Set.of(Long.class, long.class, BigInteger.class));
        ALLOWED_TYPES.put(JDBCType.FLOAT, Set.of(Float.class, float.class, Double.class, double.class, BigDecimal.class));
        ALLOWED_TYPES.put(JDBCType.DOUBLE, Set.of(Double.class, double.class, BigDecimal.class));
        ALLOWED_TYPES.put(JDBCType.DECIMAL, Set.of(BigDecimal.class));
        ALLOWED_TYPES.put(JDBCType.NUMERIC, Set.of(BigDecimal.class));
        ALLOWED_TYPES.put(JDBCType.CHAR, Set.of(String.class));
        ALLOWED_TYPES.put(JDBCType.VARCHAR, Set.of(String.class));
        ALLOWED_TYPES.put(JDBCType.LONGVARCHAR, Set.of(String.class));
        ALLOWED_TYPES.put(JDBCType.NCHAR, Set.of(String.class));
        ALLOWED_TYPES.put(JDBCType.NVARCHAR, Set.of(String.class));
        ALLOWED_TYPES.put(JDBCType.LONGNVARCHAR, Set.of(String.class));
        ALLOWED_TYPES.put(JDBCType.DATE, Set.of(Date.class, LocalDate.class));
        ALLOWED_TYPES.put(JDBCType.TIME, Set.of(Time.class, LocalTime.class));
        ALLOWED_TYPES.put(JDBCType.TIMESTAMP, Set.of(Timestamp.class, LocalDateTime.class));
        ALLOWED_TYPES.put(JDBCType.TIME_WITH_TIMEZONE, Set.of(OffsetTime.class));
        ALLOWED_TYPES.put(JDBCType.TIMESTAMP_WITH_TIMEZONE, Set.of(OffsetDateTime.class));
        ALLOWED_TYPES.put(JDBCType.BINARY, Set.of(byte[].class));
        ALLOWED_TYPES.put(JDBCType.VARBINARY, Set.of(byte[].class));
        ALLOWED_TYPES.put(JDBCType.LONGVARBINARY, Set.of(byte[].class));
    }

    public static boolean isAllowedType(JDBCType jdbcType, Class<?> fieldType) {
        Objects.requireNonNull(jdbcType, "jdbcType 은 필수입니다.");
        Objects.requireNonNull(fieldType, "fieldType 은 필수입니다.");

        final Set<Class<?>> allowedTypes = ALLOWED_TYPES.get(jdbcType);
        if (allowedTypes == null) {
            return false;
        }
        return allowedTypes.contains(fieldType);
    }



}
