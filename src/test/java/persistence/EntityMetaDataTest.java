package persistence;

import annotation.Column;
import annotation.Id;
import annotation.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityMetaDataTest {

    @DisplayName("정상 생성")
    @Test
    void init() throws Exception {
        // given
        final List<String> expectedAllColumnNames = List.of("id", "name", "age", "not_column_field");
        final List<String> expectedColumnNamesExcludeIdColumn = List.of("name", "age", "not_column_field");

        // when
        final EntityMetaData result = new EntityMetaData(NormalTestEntity.class);

        // then
        assertSoftly(assertions -> {
            assertions.assertThat(result.getTableName()).isEqualTo("test_table");
            assertions.assertThat(result.getIdColumnName()).isEqualTo("id");
            assertions.assertThat(result.getAllColumnNames()).isEqualTo(expectedAllColumnNames);
            assertions.assertThat(result.getColumnNamesExcludeIdColumn()).isEqualTo(expectedColumnNamesExcludeIdColumn);
        });
    }

    @DisplayName("엔티티에 @Table 이 없으면 클래스명을 스네이크 케이스로 변환한 것을 테이블명으로 사용한다")
    @Test
    void init1() throws Exception {
        // given
        final Class<NoTableTestEntity> entityClass = NoTableTestEntity.class;
        final String expectedTableName = "no_table_test_entity";

        // when
        final EntityMetaData result = new EntityMetaData(entityClass);

        // then
        assertThat(result.getTableName()).isEqualTo(expectedTableName);
    }

    @DisplayName("@Table 의 name 이 비어있으면 에러")
    @Test
    void init2() throws Exception {
        assertThatThrownBy(() -> new EntityMetaData(NoTableNameTestEntity.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("@Table 의 name 은 필수 입니다. entityClass: [persistence.EntityMetaDataTest$NoTableNameTestEntity]");
    }

    @DisplayName("엔티티에 @Id 가 없으면 에러")
    @Test
    void init3() throws Exception {
        assertThatThrownBy(() -> new EntityMetaData(NoIdTestEntity.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("entity 는 @Id 가 하나만 있어야 합니다. entityClass: [persistence.EntityMetaDataTest$NoIdTestEntity], idFieldNames: ");
    }

    @DisplayName("엔티티에 @Id 가 2개 이상이면 에러")
    @Test
    void init4() throws Exception {
        assertThatThrownBy(() -> new EntityMetaData(ManyIdTestEntity.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("entity 는 @Id 가 하나만 있어야 합니다. entityClass: [persistence.EntityMetaDataTest$ManyIdTestEntity], idFieldNames: id1, id2");
    }

    @DisplayName("엔티티의 필드에 @Column 이 없으면 필드명을 스네이크 케이스로 변환한 것을 컬럼명으로 사용한다")
    @Test
    void init5() throws Exception {
        // given
        final Class<NoColumnTestEntity> entityClass = NoColumnTestEntity.class;
        final String expectedIdColumnName = "entity_id";

        // when
        final EntityMetaData result = new EntityMetaData(entityClass);

        // then
        assertThat(result.getIdColumnName()).isEqualTo(expectedIdColumnName);
    }

    @DisplayName("@Column 의 name 이 비어있으면 에러")
    @Test
    void init6() throws Exception {
        assertThatThrownBy(() -> new EntityMetaData(NoColumnNameTestEntity.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("@Column 의 name 은 필수 입니다. entityClass: [persistence.EntityMetaDataTest$NoColumnNameTestEntity], field: [id]");
    }

    @Test
    void getField() throws Exception {
        // given
        final Class<NormalTestEntity> entityClass = NormalTestEntity.class;
        final EntityMetaData entityMetaData = new EntityMetaData(entityClass);

        // when
        final Optional<Field> id = entityMetaData.getField("id");
        final Optional<Field> notExistColumn = entityMetaData.getField("notExistColumn");

        // then
        final Field idField = entityClass.getDeclaredField("id");
        assertSoftly(assertions -> {
            assertions.assertThat(id).hasValue(idField);
            assertions.assertThat(notExistColumn).isEmpty();
        });
    }

    @Table(name = "test_table")
    private static class NormalTestEntity {

        @Id
        @Column(name = "id")
        private Long id;

        @Column(name = "name")
        private String name;

        @Column(name = "age")
        private int age;

        private String notColumnField;

    }

    private static class NoTableTestEntity {
        @Id
        @Column(name = "id")
        private Long id;
    }

    @Table(name = "")
    private static class NoTableNameTestEntity {
        @Id
        @Column(name = "id")
        private Long id;
    }

    @Table(name = "test_table")
    private static class NoIdTestEntity {

        @Column(name = "name")
        private String name;

    }

    @Table(name = "test_table")
    private static class ManyIdTestEntity {

        @Id
        @Column(name = "id1")
        private Long id1;

        @Id
        @Column(name = "id2")
        private Long id2;

    }

    @Table(name = "test_table")
    private static class NoColumnTestEntity {

        @Id
        private Long entityId;

    }

    @Table(name = "test_table")
    private static class NoColumnNameTestEntity {

        @Id
        @Column(name = "")
        private Long id;

    }

}