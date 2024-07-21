package booking.api.waiting.application;

import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenStatus;
import booking.common.exception.CustomNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class WaitingTokenFacadeTest {

    @Autowired
    WaitingTokenFacade waitingTokenFacade;

    @Test
    @DisplayName("사용자가 없는 경우")
    void notFoundUser() {

        long userId = -1L;
        long concertId = 1L;

        assertThatThrownBy(() -> waitingTokenFacade.issueTokenOrSearchWaiting(userId, concertId))
                .isInstanceOf(CustomNotFoundException.class)
                .hasMessage("[USER_IS_NOT_FOUND] 해당하는 유저가 없습니다. [userId : %d]".formatted(userId));
    }

    @Test
    @DisplayName("콘서트가 없는 경우")
    void notFoundConcert() {

        long userId = 1L;
        long concertId = -1L;

        assertThatThrownBy(() -> waitingTokenFacade.issueTokenOrSearchWaiting(userId, concertId))
                .isInstanceOf(CustomNotFoundException.class)
                .hasMessage("[CONCERT_IS_NOT_FOUND] 해당하는 콘서트가 없습니다. [concertId : %d]".formatted(concertId));
    }

    @Test
    @DisplayName("토큰이 없는 유저가 요청할 경우 새로운 대기열 토큰 발급")
    void issueNewToken() {

        long userId = 2L;
        long concertId = 1L;
        WaitingToken waitingToken = waitingTokenFacade.issueTokenOrSearchWaiting(userId, concertId);

        assertNotNull(waitingToken.getToken());
    }

    @Test
    @DisplayName("기존 대기열 토큰 검증")
    void searchWaiting() {

        String token = "valid-token";
        long userId = 1L;
        long concertId = 1L;

        WaitingToken waitingToken = waitingTokenFacade.issueTokenOrSearchWaiting(userId, concertId);

        //동일한 토큰인지 검증
        assertThat(waitingToken.getToken()).isEqualTo(token);

        //토큰 상태가 ACTIVATE 로 변경되었는지 검증
        assertThat(waitingToken.getWaitingTokenStatus()).isEqualTo(WaitingTokenStatus.ACTIVATE);
    }

    @Test
    @DisplayName("유저 대기 순서 검증")
    void getRank() {

        long waitingTokenId = 1L;
        long rank = waitingTokenFacade.getRank(waitingTokenId);

        //첫 번째 순서 검증
        assertThat(rank).isEqualTo(1);
    }
}