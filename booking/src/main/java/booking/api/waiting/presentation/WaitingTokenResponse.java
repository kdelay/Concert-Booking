package booking.api.waiting.presentation;

import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenStatus;
import booking.api.waiting.infrastructure.WaitingTokenEntity;
import booking.api.waiting.infrastructure.WaitingTokenMapper;

public record WaitingTokenResponse(
        String accessToken, //대기열 토큰
        WaitingTokenStatus status, //대기열 토큰 상태
        long rank //순번
) {
    public static WaitingTokenResponse of(WaitingToken waitingToken, long rank) {
        WaitingTokenEntity entity = WaitingTokenMapper.toEntity(waitingToken);
        return new WaitingTokenResponse(entity.getToken(), entity.getWaitingTokenStatus(), rank);
    }
}