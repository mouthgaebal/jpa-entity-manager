package query;

import util.CollectionUtils;
import util.Preconditions;
import util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class SelectQueryBuilder {

    private static final String ASTERISK = "*";

    private List<String> columns;
    private String table;
    private String orderByColumn;
    private Direction direction;
    private int limit;
    private String whereCondition;

    /**
     * SELECT 절 지정
     * @param columns 컬럼명 (가변 인자)
     * @return this (메서드 체이닝)
     */
    public SelectQueryBuilder select(String... columns) {
        checkColumns(columns);
        this.columns = Arrays.asList(columns);
        return this;
    }

    private void checkColumns(String[] columns) {
        Preconditions.checkArgument(StringUtils.allNotBlank(columns), "columns 는 '*' 또는 하나 이상의 컬럼명을 입력해야 합니다. columns: %s", Arrays.toString(columns));
        if (columns.length > 1) {
            checkContainsAsterisk(columns);
        }
    }

    private void checkContainsAsterisk(String[] columns) {
        for (String column : columns) {
            if (ASTERISK.equals(column)) {
                throw new IllegalArgumentException("columns 에 '*' 는 다른 컬럼명과 함께 사용할 수 없습니다. columns: %s".formatted(Arrays.toString(columns)));
            }
        }
    }

    /**
     * FROM 절 지정
     * @param table 테이블명
     * @return this
     */
    public SelectQueryBuilder from(String table) {
        Preconditions.checkArgument(StringUtils.isNotBlank(table), "table 은 null 또는 빈 문자열일 수 없습니다.");
        this.table = table;
        return this;
    }

    /**
     * ORDER BY 절 지정
     * @param column 정렬 컬럼
     * @param direction "ASC" 또는 "DESC"
     * @return this
     */
    public SelectQueryBuilder orderBy(String column, String direction) {
        Preconditions.checkArgument(StringUtils.isNotBlank(column), "column 은 null 또는 빈 문자열일 수 없습니다.");
        this.orderByColumn = column;
        this.direction = Direction.from(direction);
        return this;
    }

    /**
     * LIMIT 절 지정
     * @param limit 조회 개수
     * @return this
     */
    public SelectQueryBuilder limit(int limit) {
            Preconditions.checkArgument(limit > 0, "limit 는 0보다 큰 정수여야 합니다. limit: %d", limit);
            this.limit = limit;
        return this;
    }

    public SelectQueryBuilder where(String condition) {
        Preconditions.checkArgument(StringUtils.isNotBlank(condition), "WHERE 조건은 null 또는 빈 문자열일 수 없습니다.");
        this.whereCondition = condition;
        return this;
    }

    /**
     * SQL 문자열 생성
     * @return 생성된 SQL
     */
    public String build() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        if (CollectionUtils.isEmpty(columns)) {
            builder.append(ASTERISK);
        } else {
            builder.append(String.join(", ", columns));
        }
        if (StringUtils.isBlank(table)) {
            throw new IllegalStateException("from 절이 지정되지 않았습니다.");
        }
        builder.append("\n");
        builder.append("FROM ").append(table);
        if (StringUtils.isNotBlank(whereCondition)) {
            builder.append("\n");
            builder.append("WHERE ").append(whereCondition);
        }

        if (StringUtils.isNotBlank(orderByColumn)) {
            builder.append("\n");
            builder.append("ORDER BY ").append(orderByColumn).append(" ").append(direction.name());
        }

        if (limit > 0) {
            builder.append("\n");
            builder.append("LIMIT ").append(limit);
        }
        return builder.toString();
    }
}
