package query;

import util.MapUtils;
import util.Preconditions;
import util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class InsertQueryBuilder {

    private String table;
    private Map<String, String> columnValues = new LinkedHashMap<>();

    /**
     * INSERT할 테이블 지정
     */
    public InsertQueryBuilder into(String table) {
        Preconditions.checkArgument(StringUtils.isNotBlank(table), "테이블 이름은 비어있을 수 없습니다.");
        this.table = table;
        return this;
    }

    /**
     * 컬럼-값 쌍 추가
     * @param column 컬럼명
     * @param value 값 (?, 파라미터 플레이스홀더)
     */
    public InsertQueryBuilder value(String column, String value) {
        Preconditions.checkArgument(StringUtils.allNotBlank(column, value), "컬럼명과 값은 비어있을 수 없습니다. column: [%s], value: [%s]", column, value);
        columnValues.put(column, value);
        return this;
    }

    /**
     * 여러 컬럼-값 쌍을 Map으로 추가
     */
    public InsertQueryBuilder values(Map<String, String> columnValues) {
        Preconditions.checkArgument(MapUtils.isNotEmpty(columnValues), "columnValues는 null 이거나 비어있을 수 없습니다.");
        this.columnValues = new LinkedHashMap<>(columnValues);
        return this;
    }

    /**
     * SQL 생성
     */
    public String build() {
        Preconditions.checkState(StringUtils.isNotBlank(table), "테이블 이름이 지정되지 않았습니다.");
        Preconditions.checkState(MapUtils.isNotEmpty(columnValues), "컬럼-값 쌍이 지정되지 않았습니다.");
        final StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(table).append("\n");
        sql.append(" (").append(String.join(", ", columnValues.keySet())).append(")").append("\n");
        sql.append(" VALUES (").append(String.join(", ", columnValues.values())).append(")");
        return sql.toString();
    }
}
