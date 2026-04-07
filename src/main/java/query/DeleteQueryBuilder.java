package query;

import util.Preconditions;
import util.StringUtils;

public class DeleteQueryBuilder {

    private String table;
    private String whereCondition;

    /**
     * DELETE할 테이블 지정
     */
    public DeleteQueryBuilder from(String table) {
        Preconditions.checkArgument(StringUtils.isNotBlank(table), "테이블 이름은 비어있을 수 없습니다.");
        this.table = table;
        return this;
    }

    /**
     * WHERE 절 추가
     */
    public DeleteQueryBuilder where(String condition) {
        Preconditions.checkArgument(StringUtils.isNotBlank(condition), "WHERE 조건은 비어있을 수 없습니다.");
        this.whereCondition = condition;
        return this;
    }

    /**
     * SQL 생성
     */
    public String build() {
        Preconditions.checkState(StringUtils.isNotBlank(table), "테이블 이름이 지정되지 않았습니다.");
        final StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(table);
        if (StringUtils.isNotBlank(whereCondition)) {
            sql.append("\n").append("WHERE ").append(whereCondition);
        }
        return sql.toString();
    }
}
