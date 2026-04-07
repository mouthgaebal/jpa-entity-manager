package persistence;

import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PersistenceContextTest {

    private PersistenceContext persistenceContext;

    @BeforeEach
    void setUp() {
        persistenceContext = new PersistenceContext();
    }

    @DisplayName("get - 조회 결과가 없는 경우 null 을 반환한다")
    @Test
    void get() {
        // given
        final Class<User> entityClass = User.class;
        final EntityId entityId = new EntityId(1L);

        // when
        final User result = persistenceContext.get(entityClass, entityId);

        // then
        assertThat(result).isNull();
    }

    @DisplayName("put - 엔티티를 저장한다")
    @Test
    void put() {
        // given
        final Class<User> entityClass = User.class;
        final EntityId entityId = new EntityId(1L);
        final User user = new User(1L, "test-user", 33);

        // when
        persistenceContext.put(entityClass, entityId, user);

        // then
        final User result = persistenceContext.get(entityClass, entityId);
        assertThat(result).isSameAs(user);
    }

}