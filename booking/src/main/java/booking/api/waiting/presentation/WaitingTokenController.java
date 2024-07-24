package booking.api.waiting.presentation;

import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenService;
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

    private final WaitingTokenService waitingTokenService;

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
        WaitingToken result = waitingTokenService.issueToken(waitingTokenRequest.userId());
        long rank = waitingTokenService.getRank(result.getId());
        return WaitingTokenResponse.of(result, rank+1);
    }
}