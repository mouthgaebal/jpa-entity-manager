package persistence;


import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerBasicTest {

    @Test
    void EntityManager는_Connection을_관리한다() throws SQLException {
        Connection conn = createConnection();

        EntityManager em = new EntityManager(conn);

        assertNotNull(em);
        assertFalse(conn.isClosed());
        
        em.close();

        assertTrue(conn.isClosed());
    }

    @Test
    void EntityManager는_PersistenceContext를_생성한다() {
        Connection conn = createConnection();

        EntityManager em = new EntityManager(conn);

        assertNotNull(em.getPersistenceContext());
    }

    @Test
    void EntityManager는_Transaction을_관리한다() throws SQLException {

        Connection conn = createConnection();
        EntityManager em = new EntityManager(conn);

        // When: Transaction 시작
        em.getTransaction().begin();

        assertFalse(conn.getAutoCommit());

        // When: Transaction 종료
        em.getTransaction().commit();

        assertTrue(conn.getAutoCommit());

        em.close();
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
