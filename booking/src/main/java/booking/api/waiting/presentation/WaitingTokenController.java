package booking.api.waiting.presentation;

import booking.api.waiting.application.WaitingTokenFacade;
import booking.api.waiting.domain.WaitingToken;
import booking.support.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/waiting/token")
public class WaitingTokenController {

    private final WaitingTokenFacade waitingTokenFacade;

    /**
     * 대기열 토큰 발급 및 조회
     * @param waitingTokenRequest userId, concertId
     * @return 대기열 정보
     */
    @Authorization
    @PostMapping
    public WaitingTokenResponse issueTokenOrSearchWaiting(
            @RequestBody WaitingTokenRequest waitingTokenRequest
    ) {
        WaitingToken result = waitingTokenFacade.issueTokenOrSearchWaiting(waitingTokenRequest.userId(), waitingTokenRequest.concertId());
        long rank = waitingTokenFacade.getRank(result.getId());
        return WaitingTokenResponse.of(result, rank+1);
    }
}