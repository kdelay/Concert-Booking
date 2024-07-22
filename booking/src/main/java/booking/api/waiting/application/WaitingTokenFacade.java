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

    public WaitingToken issueTokenOrSearchWaiting(Long userId, Long concertId) {
        return waitingTokenService.issueTokenOrSearchWaiting(userId, concertId);
    }

    public List<WaitingToken> getDeactivateTokens() {
        return waitingTokenService.getDeactivateTokens();
    }

    public void checkEntryAndUpdateState(WaitingToken waitingToken) {
        waitingTokenService.checkEntryAndUpdateState(waitingToken);
    }

    public long getRank(Long waitingTokenId) {
        return waitingTokenService.getRank(waitingTokenId);
    }
}
