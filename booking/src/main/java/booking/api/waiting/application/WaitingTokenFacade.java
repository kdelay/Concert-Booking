package booking.api.waiting.application;

import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WaitingTokenFacade {

    private final WaitingTokenService waitingTokenService;

    //대기열 토큰 발급 및 조회
    public WaitingToken issueTokenOrSearchWaiting(Long userId, Long concertId) {
        return waitingTokenService.issueTokenOrSearchWaiting(userId, concertId);
    }

    //DEACTIVATE 상태인 대기열 토큰 3개 조회
    public List<WaitingToken> getDeactivateTokens() {
        return waitingTokenService.getDeactivateTokens();
    }

    //대기열 입장 가능 시간 확인 및 입장 시 토큰 상태 변경
    public void checkEntryAndUpdateState(WaitingToken waitingToken) {
        waitingTokenService.checkEntryAndUpdateState(waitingToken);
    }

    //토큰을 가지고 있는 유저의 현재 대기열 순서 번호 반환
    public long getRank(Long waitingTokenId) {
        return waitingTokenService.getRank(waitingTokenId);
    }
}
