package booking.api.waiting.presentation;

import booking.api.waiting.domain.WaitingTokenStatus;

public record WaitingTokenResponse(
        String accessToken, //대기열 토큰
        WaitingTokenStatus status, //대기열 토큰 상태
        long rank //순번
) {
}