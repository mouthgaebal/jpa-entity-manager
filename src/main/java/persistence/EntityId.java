package persistence;

import java.util.Objects;

public class EntityId {

    private final Object id;

    public EntityId(Object id) {
        Objects.requireNonNull(id, "id 는 필수 입니다.");
        this.id = id;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof EntityId entityId)) {
            return false;
        }

        return Objects.equals(id, entityId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
