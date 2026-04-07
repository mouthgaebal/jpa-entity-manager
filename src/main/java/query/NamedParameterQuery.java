package query;

import util.CollectionUtils;
import util.Preconditions;
import util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.function.Predicate.not;

public class NamedParameterQuery {

    private static final String PARAMETER_NAME_REGEX = ":\\w+";
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER_NAME_REGEX);
    
    private final Map<String, Object> parameters = new HashMap<>();
    private final LinkedHashSet<String> parameterNames;

    private final String sql;

    /**
     * Named Parameter를 지원하는 쿼리 생성
     * @param sql Named Parameter가 포함된 SQL (:paramName 형식)
     */
    public NamedParameterQuery(String sql) {
        Preconditions.checkArgument(StringUtils.isNotBlank(sql), "sql 은 필수 입니다.");
        this.parameterNames = extractParameterIndexes(sql);
        this.sql = sql.replaceAll(PARAMETER_NAME_REGEX, "?");
    }

    private LinkedHashSet<String> extractParameterIndexes(String sql) {
        final LinkedHashSet<String> results = new LinkedHashSet<>();
        final Matcher matcher = PARAMETER_PATTERN.matcher(sql);
        while (matcher.find()) {
            final String parameterName = matcher.group().substring(1);
            if (!results.add(parameterName)) {
                throw new IllegalArgumentException("중복된 파라미터 이름이 있습니다. parameterName: [%s]".formatted(parameterName));
            }
        }
        return results;
    }


    /**
     * 파라미터 값 설정
     * @param name 파라미터 이름
     * @param value 파라미터 값
     */
    public NamedParameterQuery setParameter(String name, Object value) {
        Preconditions.checkArgument(StringUtils.isNotBlank(name), "name 은 필수 입니다.");
        Objects.requireNonNull(value, "value 는 null 일 수 없습니다.");
        parameters.put(name, value);
        return this;
    }

    /**
     * JDBC용 SQL로 변환 (? 플레이스홀더)
     * @return 변환된 SQL
     */
    public String toJdbcSql() {
        return sql;
    }

    /**
     * PreparedStatement에 파라미터 바인딩
     * @param pstmt PreparedStatement
     */
    public void bindParameters(PreparedStatement pstmt) throws SQLException {
        validateNotExistParameters();
        int index = 1;
        for (String parameterName : parameterNames) {
            final Object value = parameters.get(parameterName);
            pstmt.setObject(index, value);
            index++;
        }
    }

    private void validateNotExistParameters() {
        final List<String> notExistParameters = parameterNames.stream()
                .filter(not(parameters::containsKey))
                .toList();
        if (CollectionUtils.isNotEmpty(notExistParameters)) {
            throw new IllegalStateException("값이 설정되지 않은 파라미터가 있습니다. parameterNames: " + notExistParameters);
        }
    }

}
