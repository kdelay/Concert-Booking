package booking.api.waiting.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;;

    @Mock
    WaitingTokenRepository waitingTokenRepository;

    @Test
    @DisplayName("충전 금액이 없는 경우")
    void amountIsZero() {

        Long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(0);

        assertThatThrownBy(() -> userService.charge(userId, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전 금액이 0원입니다.");
    }

    @Test
    @DisplayName("충전 성공")
    void charge() {

        Long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(500);
        User user = User.create(userId, BigDecimal.ZERO);

        when(waitingTokenRepository.findByUserId(userId)).thenReturn(user);

        User chargeUser = userService.charge(userId, amount);
        assertThat(chargeUser.getAmount()).isEqualTo(BigDecimal.valueOf(500));
    }
}