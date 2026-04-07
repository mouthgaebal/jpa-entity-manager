package query;

import util.MapUtils;
import util.Preconditions;
import util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    private String table;
    private Map<String, String> columnValues = new LinkedHashMap<>();
    private String whereCondition;

    /**
     * UPDATE할 테이블 지정
     */
    public UpdateQueryBuilder table(String table) {
        Preconditions.checkArgument(StringUtils.isNotBlank(table), "테이블 이름은 비어있을 수 없습니다.");
        this.table = table;
        return this;
    }

    /**
     * SET 절에 컬럼-값 추가
     */
    public UpdateQueryBuilder set(String column, String value) {
        Preconditions.checkArgument(StringUtils.allNotBlank(column, value), "컬럼명과 값은 비어있을 수 없습니다. column: [%s], value: [%s]", column, value);
        columnValues.put(column, value);
        return this;
    }

    /**
     * WHERE 절 추가
     */
    public UpdateQueryBuilder where(String condition) {
        Preconditions.checkArgument(StringUtils.isNotBlank(condition), "WHERE 조건은 비어있을 수 없습니다.");
        this.whereCondition = condition;
        return this;
    }

    /**
     * SQL 생성
     */
    public String build() {
        Preconditions.checkState(StringUtils.isNotBlank(table), "테이블 이름이 지정되지 않았습니다.");
        Preconditions.checkState(MapUtils.isNotEmpty(columnValues), "컬럼-값 쌍이 지정되지 않았습니다.");
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(table).append("\n");
        sql.append("SET ").append(convertToSqlColumns());
        if (StringUtils.isNotBlank(whereCondition)) {
            sql.append("\n").append("WHERE ").append(whereCondition);
        }
        return sql.toString();
    }

    private String convertToSqlColumns() {
        return columnValues.entrySet().stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

}
