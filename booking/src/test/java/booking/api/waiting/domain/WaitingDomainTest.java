package booking.api.waiting.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static booking.api.waiting.domain.WaitingTokenStatus.ACTIVATE;
import static booking.api.waiting.domain.WaitingTokenStatus.EXPIRED;
import static org.assertj.core.api.Assertions.assertThat;

public class WaitingDomainTest {

    private User user;
    private WaitingToken waitingToken;

    @BeforeEach
    void setUp() {
        user = new User(1L, BigDecimal.valueOf(2000));
        waitingToken = new WaitingToken(1L, user, "valid-token", WaitingTokenStatus.DEACTIVATE, LocalDateTime.now(), null);
    }

    @Test
    @DisplayName("유저 잔액 충전")
    void chargeAmount() {

        BigDecimal amount = BigDecimal.valueOf(2000);
        User result = user.chargeAmount(amount);

        //동일하면 0 반환
        assertThat(result.getAmount().compareTo(BigDecimal.valueOf(4000))).isEqualTo(0);
    }

    @Test
    @DisplayName("유저 잔액 차감")
    void useAmount() {

        BigDecimal amount = BigDecimal.valueOf(2000);
        User result = user.useAmount(amount);

        //동일하면 0 반환
        assertThat(result.getAmount().compareTo(BigDecimal.valueOf(0))).isEqualTo(0);
    }

    @Test
    @DisplayName("토큰 생성 시간 ms 로 변경")
    void getCreatedAtToMilli() {

        LocalDateTime now = LocalDateTime.of(2024, 7, 19, 0, 0, 0);
        ZonedDateTime zonedDateTime = now.atZone(ZoneId.systemDefault());
        long expectedMillis = zonedDateTime.toInstant().toEpochMilli();

        long actualMillis = WaitingToken.getCreatedAtToMilli(now);

        assertThat(actualMillis).isEqualTo(expectedMillis);
    }

    @Test
    @DisplayName("대기열 토큰 상태 변경")
    void updateWaitingTokenStatus() {

        waitingToken.updateWaitingTokenStatus(ACTIVATE);
        assertThat(waitingToken.getWaitingTokenStatus()).isEqualTo(ACTIVATE);

        waitingToken.updateWaitingTokenStatus(EXPIRED);
        assertThat(waitingToken.getWaitingTokenStatus()).isEqualTo(EXPIRED);
    }

    @Test
    @DisplayName("변경 시간 업데이트")
    void updateModifiedAt() {

        LocalDateTime now = LocalDateTime.now();
        waitingToken.updateModifiedAt(now);
        assertThat(waitingToken.getModifiedAt()).isEqualTo(now);
    }
}
