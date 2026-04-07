package persistence;

import jdbc.QueryExecutor;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


class ConnectionOwnershipTest {

    @Test
    void Connection은_EntityManager가_소유한다() throws SQLException {
        Connection connection = DriverManager.getConnection(
            "jdbc:h2:mem:test", "sa", ""
        );
        assertFalse(connection.isClosed());

        // When: EntityManager에게 소유권 이전
        SimpleEntityManager em = new SimpleEntityManager(connection);

        // Then: EntityManager가 관리 중
        assertNotNull(em.getConnection());
        assertFalse(em.getConnection().isClosed());

        // When: EntityManager 종료
        em.close();

        // Then: Connection도 같이 닫힘
        assertTrue(connection.isClosed());
    }

    @Test
    void 하위_컴포넌트는_Connection을_닫지_않는다() throws SQLException {
        // Given
        Connection connection = DriverManager.getConnection(
            "jdbc:h2:mem:test", "sa", ""
        );

        // When: 하위 컴포넌트가 Connection 사용
        QueryExecutor executor = new QueryExecutor(connection);
        executor.executeQuery("SELECT 1");

        // Then: 하위 컴포넌트는 Connection을 닫지 않음
        assertFalse(connection.isClosed());

        // Cleanup
        connection.close();
    }
}
