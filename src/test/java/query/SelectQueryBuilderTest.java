package query;

import annotation.BlankSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import testutil.TestArrayUtils;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelectQueryBuilderTest {

    @ParameterizedTest(name = "select - null 또는 빈 문자열이 포함된 경우 에러. columns: {0}")
    @MethodSource("select")
    void select(String[] columns) throws Exception {
        assertThatThrownBy(() -> new SelectQueryBuilder().select(columns))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("columns 는 '*' 또는 하나 이상의 컬럼명을 입력해야 합니다. columns: %s", Arrays.toString(columns));
    }

    private static Stream<Arguments> select() {
        return Stream.of(
                TestArrayUtils.toArguments(null),
                TestArrayUtils.toArguments(new String[]{null}),
                TestArrayUtils.toArguments(new String[]{""}),
                TestArrayUtils.toArguments(new String[]{" "}),
                TestArrayUtils.toArguments(new String[]{"a", null}),
                TestArrayUtils.toArguments(new String[]{"a", " "}),
                TestArrayUtils.toArguments(new String[]{null, "a"}),
                TestArrayUtils.toArguments(new String[]{" ", "a"}),
                TestArrayUtils.toArguments(new String[]{null, ""}),
                TestArrayUtils.toArguments(new String[]{null, " "}),
                TestArrayUtils.toArguments(new String[]{"", null}),
                TestArrayUtils.toArguments(new String[]{" ", null})
        );
    }

    @ParameterizedTest(name = "select - '*' 와 다른 컬럼명이 함께 포함된 경우 에러. columns: {0}")
    @MethodSource("select1")
    void select1(String[] columns) throws Exception {
        assertThatThrownBy(() -> new SelectQueryBuilder().select(columns))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("columns 에 '*' 는 다른 컬럼명과 함께 사용할 수 없습니다. columns: %s", Arrays.toString(columns));
    }

    private static Stream<Arguments> select1() {
        return Stream.of(
                TestArrayUtils.toArguments(new String[]{"*", "a"}),
                TestArrayUtils.toArguments(new String[]{"a", "*"})
        );
    }

    @ParameterizedTest(name = "from - null 또는 빈 문자열인 경우 에러. table: [{0}]")
    @BlankSource
    void from(String table) throws Exception {
        assertThatThrownBy(() -> new SelectQueryBuilder().from(table))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("table 은 null 또는 빈 문자열일 수 없습니다.");
    }

    @ParameterizedTest(name = "orderBy - columns 가 null 또는 빈 문자열인 경우 에러. column: [{0}]")
    @BlankSource
    void orderBy(String columns) throws Exception {
        assertThatThrownBy(() -> new SelectQueryBuilder().orderBy(columns, "ASC"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("column 은 null 또는 빈 문자열일 수 없습니다.");
    }

    @ParameterizedTest(name = "orderBy - direction 이 'ASC' 또는 'DESC' 가 아닌 경우 에러. direction: [{0}]")
    @MethodSource("orderBy1")
    void orderBy1(String direction) throws Exception {
        assertThatThrownBy(() -> new SelectQueryBuilder().orderBy("column", direction))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("direction 은 'ASC' 또는 'DESC' 여야 합니다. direction: %s", direction);
    }

    private static Object[][] orderBy1() {
        return new Object[][] {
                { null },
                { "" },
                { " " },
                { "a" }
        };
    }

    @ParameterizedTest(name = "limit - limit 이 0 이하인 경우 에러. limit: {0}")
    @CsvSource({
        "0",
        "-1"
    })
    void limit(int limit) throws Exception {
        assertThatThrownBy(() -> new SelectQueryBuilder().limit(limit))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("limit 는 0보다 큰 정수여야 합니다. limit: %d", limit);
    }

    @ParameterizedTest(name = "where - condition 이 null 또는 빈 문자열인 경우 에러. condition: [{0}]")
    @BlankSource
    void where(String condition) throws Exception {
        assertThatThrownBy(() -> new SelectQueryBuilder().where(condition))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WHERE 조건은 null 또는 빈 문자열일 수 없습니다.");
    }

    @Nested
    class BuildSelectTest {

        @DisplayName("build - SELECT 절이 지정되지 않은경우 SELECT * 로 설정")
        @Test
        void buildSelect() throws Exception {
            final String query = new SelectQueryBuilder()
                    .from("table")
                    .build();
            assertThat(query).startsWith("SELECT *");
        }

        @ParameterizedTest(name = "build - SELECT 절에 컬럼이 지정된 경우 해당 컬럼으로 설정. columns: {0}, expected: {1}")
        @MethodSource("buildSelect1")
        void buildSelect1(String[] columns, String expected) throws Exception {
            final String query = new SelectQueryBuilder()
                    .select(columns)
                    .from("table")
                    .build();
            assertThat(query).startsWith("SELECT %s".formatted(expected));
        }

        private static Stream<Arguments> buildSelect1() {
            return Stream.of(
                    Arguments.of(new String[] {"*"}, "*"),
                    Arguments.of(new String[] {"column1"}, "column1"),
                    Arguments.of(new String[] {"column1", "column2"}, "column1, column2")
            );
        }

        @DisplayName("build - SELECT 절이 여러번 호출 될 경우 마지막에 지정된 컬럼으로 설정")
        @Test
        void buildSelect2() throws Exception {
            final String query = new SelectQueryBuilder()
                    .select("column1")
                    .select("column2")
                    .from("table")
                    .select("column3")
                    .build();
            assertThat(query).startsWith("SELECT column3");
        }
    }

    @Nested
    class BuildFromTest {

        @DisplayName("build - from 절이 지정되지 않은 경우 에러")
        @Test
        void buildFrom() throws Exception {
            assertThatThrownBy(() -> new SelectQueryBuilder().build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("from 절이 지정되지 않았습니다.");
        }

        @DisplayName("build - FROM 절이 지정된 경우 해당 테이블로 설정")
        @Test
        void buildFrom1() throws Exception {
            final String query = new SelectQueryBuilder()
                    .from("table")
                    .build();
            assertThat(query).endsWith("FROM table");
        }

        @DisplayName("build - FROM 절이 여러번 호출 될 경우 마지막에 지정된 테이블로 설정")
        @Test
        void buildFrom2() throws Exception {
            final String query = new SelectQueryBuilder()
                    .from("table1")
                    .from("table2")
                    .build();
            assertThat(query).endsWith("FROM table2");
        }

    }

    @Nested
    class BuildOrderByTest {
        @DisplayName("build - ORDER BY 절이 지정된 경우 해당 컬럼과 방향으로 설정")
        @Test
        void buildOrderBy() throws Exception {
            final String query = new SelectQueryBuilder()
                    .from("table")
                    .orderBy("column", "DESC")
                    .build();
            assertThat(query).endsWith("ORDER BY column DESC");
        }

        @DisplayName("build - ORDER BY 절이 여러번 호출 될 경우 마지막에 지정된 컬럼과 방향으로 설정")
        @Test
        void buildOrderBy1() throws Exception {
            final String query = new SelectQueryBuilder()
                    .orderBy("column1", "ASC")
                    .from("table")
                    .orderBy("column2", "DESC")
                    .build();
            assertThat(query).endsWith("ORDER BY column2 DESC");
        }
    }

    @Nested
    class BuildLimitTest {
        @DisplayName("build - LIMIT 절이 지정된 경우 해당 개수로 설정")
        @Test
        void buildLimit() throws Exception {
            final String query = new SelectQueryBuilder()
                    .from("table")
                    .limit(10)
                    .build();
            assertThat(query).endsWith("LIMIT 10");
        }

        @DisplayName("build - LIMIT 절이 여러번 호출 될 경우 마지막에 지정된 개수로 설정")
        @Test
        void buildLimit1() throws Exception {
            final String query = new SelectQueryBuilder()
                    .limit(10)
                    .from("table")
                    .limit(20)
                    .build();
            assertThat(query).endsWith("LIMIT 20");
        }
    }

    @ParameterizedTest(name = "build - 전체 쿼리 빌드 테스트. index: [{index}]")
    @MethodSource("build")
    void build(String result, String expected) throws Exception {
        assertThat(result).isEqualTo(expected);
    }

    private static Object[][] build() {
            return new Object[][] {
                    {
                            new SelectQueryBuilder()
                                    .from("table")
                                    .build(),
                            """
                            SELECT *
                            FROM table
                            """.stripTrailing()
                    },
                    {
                            new SelectQueryBuilder()
                                    .select("*")
                                    .from("table")
                                    .orderBy("column1", "DESC")
                                    .build(),
                            """
                            SELECT *
                            FROM table
                            ORDER BY column1 DESC
                            """.stripTrailing()
                    },
                    {
                            new SelectQueryBuilder()
                                    .select("column1", "column2")
                                    .from("table")
                                    .orderBy("column1", "ASC")
                                    .limit(10)
                                    .build(),
                            """
                            SELECT column1, column2
                            FROM table
                            ORDER BY column1 ASC
                            LIMIT 10
                            """.stripTrailing()
                    },
                    {
                            new SelectQueryBuilder()
                                    .select("column1")
                                    .from("table")
                                    .orderBy("column1", "ASC")
                                    .limit(20)
                                    .where("column1 > 10")
                                    .build(),
                            """
                            SELECT column1
                            FROM table
                            WHERE column1 > 10
                            ORDER BY column1 ASC
                            LIMIT 20
                            """.stripTrailing()
                    }
            };
    }

}