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
                            "  id BIGINT PRIMARY KEY AUTO_INCREMENT," +
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
            stmt.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

            stmt.execute("INSERT INTO users (name, age) VALUES ('John', 30)");
            stmt.execute("INSERT INTO users (name, age) VALUES ('Alice', 25)");
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

    @Test
    void persist는_즉시_실행되지_않는다() throws Exception {
        // Given
        SimpleEntityManager entityManager = new SimpleEntityManager(connection);
        entityManager.getTransaction().begin();
        User user = new User("John", 25);

        // When
        entityManager.persist(user);

        // Then
        // 이 시점에는 DB에 없어야 함 (아직 flush 안됨)
        assertNull(findInDatabase(3L));

        entityManager.getTransaction().rollback();
    }

    @Test
    void flush_시점에_INSERT가_실행된다() throws Exception {
        // Given
        SimpleEntityManager entityManager = new SimpleEntityManager(connection);
        entityManager.getTransaction().begin();
        User user = new User("NewUser", 25);
        entityManager.persist(user);

        // When
        entityManager.flush();

        // Then
        // flush() 후에는 DB에 있어야 함
        assertNotNull(findInDatabase(3L));

        entityManager.getTransaction().commit();
    }

    @Test
    void 여러_persist를_모아서_실행한다() throws Exception {
        // Given
        SimpleEntityManager entityManager = new SimpleEntityManager(connection);
        entityManager.getTransaction().begin();

        // When
        for (int i = 0; i < 100; i++) {
            entityManager.persist(new User("User" + i, 20 + i));
        }
        entityManager.flush();  // 한 번에 실행!

        // Then
        assertEquals(102, countUsers());  // 기존 2개 + 새로 추가한 100개

        entityManager.getTransaction().commit();
    }

    private User findInDatabase(Long id) throws SQLException {
        try (Statement stmt = connection.createStatement();
             var rs = stmt.executeQuery("SELECT * FROM users WHERE id = " + id)) {
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setAge(rs.getInt("age"));
                return user;
            }
            return null;
        }
    }

    private int countUsers() throws SQLException {
        try (Statement stmt = connection.createStatement();
             var rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

}
