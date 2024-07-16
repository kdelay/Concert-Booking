package booking.api.waiting.presentation;

import booking.api.waiting.application.WaitingTokenFacade;
import booking.api.waiting.domain.WaitingToken;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/waiting/token")
public class WaitingTokenController {

    private final WaitingTokenFacade waitingTokenFacade;

    /**
     * 대기열 토큰 발급 및 조회
     * @param token Auth - Bearer Token
     * @param waitingTokenRequest userId, concertId
     * @return 대기열 정보
     */
    @PostMapping
    public WaitingTokenResponse issueTokenOrSearchWaiting(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody WaitingTokenRequest waitingTokenRequest
    ) {
        WaitingToken result = waitingTokenFacade.issueTokenOrSearchWaiting(token,
                waitingTokenRequest.userId(), waitingTokenRequest.concertId());
        long rank = waitingTokenFacade.getRank(result.getId());
        return WaitingTokenResponse.of(result, rank+1);
    }
}