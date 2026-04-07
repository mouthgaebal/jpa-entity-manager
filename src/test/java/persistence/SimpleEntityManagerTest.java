package persistence;


import entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class SimpleEntityManagerTest {

    private static Connection connection;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:simpleEntityManagerTest;DB_CLOSE_DELAY=-1", "sa", ""
        );

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE users (" +
                            "  id BIGINT PRIMARY KEY," +
                            "  name VARCHAR(100)," +
                            "  age INT" +
                            ")"
            );
        }
    }

    @AfterAll
    static void teardownDatabase() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @BeforeEach
    void insertTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM users");
            stmt.execute("INSERT INTO users (id, name, age) VALUES (1, 'John', 30)");
            stmt.execute("INSERT INTO users (id, name, age) VALUES (2, 'Alice', 25)");
        }
    }

    @Test
    void 같은_ID는_같은_인스턴스를_반환한다() throws Exception {
        // Given
        SimpleEntityManager entityManager = new SimpleEntityManager(connection);

        // When
        User user1 = entityManager.find(User.class, 1L);
        User user2 = entityManager.find(User.class, 1L);

        // Then
        assertTrue(user1 == user2);  // 동일성 보장!
    }

    @Test
    void 첫_조회는_DB에서_가져온다() throws Exception {
        // Given
        SimpleEntityManager entityManager = new SimpleEntityManager(connection);

        // When
        User user = entityManager.find(User.class, 1L);

        // Then
        assertNotNull(user);
        assertEquals("John", user.getName());
        // DB 조회 확인 (로그 등)
    }

    @Test
    void 두_번째_조회는_캐시에서_가져온다() throws Exception {
        // Given
        SimpleEntityManager entityManager = new SimpleEntityManager(connection);
        User user1 = entityManager.find(User.class, 1L);  // DB 조회

        // When
        User user2 = entityManager.find(User.class, 1L);  // 캐시 조회

        // Then
        assertTrue(user1 == user2);
        // DB 조회 없이 캐시에서 반환 확인
    }

    @Test
    void 다른_ID는_다른_인스턴스를_반환한다() throws Exception {
        // Given
        SimpleEntityManager entityManager = new SimpleEntityManager(connection);

        // When
        User user1 = entityManager.find(User.class, 1L);
        User user2 = entityManager.find(User.class, 2L);

        // Then
        assertFalse(user1 == user2);
    }

}
