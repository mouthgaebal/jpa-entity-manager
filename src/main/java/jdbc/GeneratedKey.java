package jdbc;

import util.Preconditions;
import util.StringUtils;

import java.util.Objects;

public class GeneratedKey {

    private final String keyColumnName;

    private Object key;

    public GeneratedKey(String keyColumnName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(keyColumnName), "keyColumnName 은 필수 입니다.");
        this.keyColumnName = keyColumnName;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public String getKeyColumnName() {
        return keyColumnName;
    }

    public Number getKey() {
        return getKey(Number.class);
    }

    public <T> T getKey(Class<T> keyType) {
        Objects.requireNonNull(keyType, "keyType 은 필수 입니다.");
        if (key == null) {
            throw new IllegalStateException("키가 존재하지 않습니다.");
        }
        final Class<?> actualKeyType = key.getClass();
        if (!keyType.isAssignableFrom(actualKeyType)) {
            throw new IllegalArgumentException("키 타입이 일치하지 않습니다. keyType: [%s], actual: [%s]".formatted(keyType.getName(), actualKeyType.getName()));
        }
        return keyType.cast(key);
    }

}
