package util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PreconditionsTest {

    @DisplayName("checkArgument - condition 이 true 인 경우 예외가 발생하지 않는다")
    @Test
    void checkArgument() throws Exception {
        Assertions.assertDoesNotThrow(() -> Preconditions.checkArgument(true, "에러 없음"));
    }

    @DisplayName("checkArgument - condition 이 false 인 경우 IllegalArgumentException 예외가 발생한다")
    @Test
    void checkArgument1() throws Exception {
        final String message = "에러 발생 - %s";

        assertThatThrownBy(() -> Preconditions.checkArgument(false, message, "에러다!!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message, "에러다!!");
    }

    @DisplayName("checkState - condition 이 true 인 경우 예외가 발생하지 않는다")
    @Test
    void checkState() throws Exception {
        Assertions.assertDoesNotThrow(() -> Preconditions.checkState(true, "에러 없음"));
    }

    @DisplayName("checkState - condition 이 false 인 경우 IllegalStateException 예외가 발생한다")
    @Test
    void checkState1() throws Exception {
        final String message = "에러 발생 - %s";
        assertThatThrownBy(() -> Preconditions.checkState(false, message, "에러다!!"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(message, "에러다!!");
    }

}