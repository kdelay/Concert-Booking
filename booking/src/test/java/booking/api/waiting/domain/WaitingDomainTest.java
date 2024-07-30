package booking.api.waiting.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static booking.api.waiting.domain.WaitingTokenStatus.ACTIVATE;
import static booking.api.waiting.domain.WaitingTokenStatus.EXPIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class WaitingDomainTest {

    private User user;
    private WaitingToken waitingToken;

    @InjectMocks
    WaitingTokenService waitingTokenService;

    @BeforeEach
    void setUp() {
        user = new User(1L, BigDecimal.valueOf(2000));
        waitingToken = new WaitingToken(1L, 0L, user, "valid-token", WaitingTokenStatus.DEACTIVATE, LocalDateTime.now(), null);

        //value mocking
        ReflectionTestUtils.setField(waitingTokenService, "entryAmount", 3);
        ReflectionTestUtils.setField(waitingTokenService, "processTime", 60000);
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
    @DisplayName("토큰 활성화 가능 여부")
    void isEntryTime() {
        boolean isEntryTime = waitingToken.isEntryTime(1L, 3, 60000L);
        assertTrue(isEntryTime);
    }

    @Test
    @DisplayName("대기열 토큰 상태 변경")
    void updateWaitingTokenStatus() {

        waitingToken.activateToken();
        assertThat(waitingToken.getWaitingTokenStatus()).isEqualTo(ACTIVATE);

        waitingToken.expiredToken();
        assertThat(waitingToken.getWaitingTokenStatus()).isEqualTo(EXPIRED);
    }

    @Test
    @DisplayName("변경 시간 업데이트")
    void updateTime() {

        LocalDateTime now = LocalDateTime.now();
        waitingToken.updateTime();
        LocalDateTime afterUpdate = waitingToken.getModifiedAt();
        assertThat(afterUpdate.truncatedTo(ChronoUnit.MILLIS)).isEqualTo(now.truncatedTo(ChronoUnit.MILLIS));
    }
}
