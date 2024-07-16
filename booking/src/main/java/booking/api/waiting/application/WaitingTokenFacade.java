package booking.api.waiting.application;

import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingTokenFacade {

    private final WaitingTokenService waitingTokenService;

    //대기열 토큰 발급 및 조회
    public WaitingToken issueTokenOrSearchWaiting(String token, Long userId, Long concertId) {
        return waitingTokenService.issueTokenOrSearchWaiting(token, userId, concertId);
    }

    //토큰을 가지고 있는 유저의 현재 대기열 순서 번호 반환
    public long getRank(Long waitingTokenId) {
        return waitingTokenService.getRank(waitingTokenId);
    }
}
