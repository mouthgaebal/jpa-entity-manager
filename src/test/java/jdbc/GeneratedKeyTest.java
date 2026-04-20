package jdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneratedKeyTest {

    @Test
    void getKey() throws Exception {
        // given
        final Long key = 1L;

        final GeneratedKey generatedKey = new GeneratedKey("id");
        generatedKey.setKey(key);

        // when
        final Long result = generatedKey.getKey(Long.class);

        // then
        assertThat(result).isEqualTo(key);
    }

    @DisplayName("getKey - 설정된 key 가 없으면 에러")
    @Test
    void getKey1() throws Exception {
        // given
        final GeneratedKey generatedKey = new GeneratedKey("id");

        // when
        assertThatThrownBy(() -> generatedKey.getKey(Long.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("키가 존재하지 않습니다.");
    }

    @DisplayName("getKey - keyType 이 실제 key 타입으로 변환 가능하지 않으면 에러")
    @Test
    void getKey2() throws Exception {
        // given
        final GeneratedKey generatedKey = new GeneratedKey("id");
        generatedKey.setKey(1L);

        // when
        assertThatThrownBy(() -> generatedKey.getKey(Integer.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("키 타입이 일치하지 않습니다. keyType: [java.lang.Integer], actual: [java.lang.Long]");
    }

}