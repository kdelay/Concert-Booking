package booking.api.waiting.domain;

import booking.common.exception.AuthorizationException;
import booking.common.exception.CustomNotFoundException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static booking.api.waiting.domain.WaitingTokenStatus.DEACTIVATE;
import static booking.common.exception.ErrorCode.USER_IS_NOT_FOUND;
import static booking.common.exception.ErrorCode.WAITING_TOKEN_IS_NOT_FOUND;

@Getter
public class WaitingToken {

    private final Long id;
    private final User user;
    private final String token;
    private WaitingTokenStatus waitingTokenStatus;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public WaitingToken(
            Long id,
            User user,
            String token,
            WaitingTokenStatus waitingTokenStatus,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {
        if (user == null || user.getId() == null) throw new CustomNotFoundException(USER_IS_NOT_FOUND, "[WaitingToken - user/userId] is null");
        if (waitingTokenStatus == null) throw new CustomNotFoundException(WAITING_TOKEN_IS_NOT_FOUND, "[WaitingToken - status] is null");
        this.id = id;
        this.user = user;
        this.token = token;
        this.waitingTokenStatus = waitingTokenStatus;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    /**
     * 새로운 토큰 발급 시 사용
     * @param user 유저 정보
     * @return 새로운 토큰
     */
    public static WaitingToken create(
            User user
    ) {
        return new WaitingToken(
                null,
                user,
                UUID.randomUUID().toString(),
                DEACTIVATE,
                LocalDateTime.now(),
                null
        );
    }

    /**
     * @return 토큰 생성 시간(localDateTime) -> ms 시간으로 변경
     */
    public static long getCreatedAtToMilli(LocalDateTime createdAt) {
        return createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 대기열 토큰 상태 변경
     * @param waitingTokenStatus 대기열 토큰 상태(DEACTIVATE, ACTIVATE, EXPIRED)
     */
    public void updateWaitingTokenStatus(WaitingTokenStatus waitingTokenStatus) {
        this.waitingTokenStatus = waitingTokenStatus;
    }

    public static void tokenAuthorization(String token) {
        if (token == null || token.isEmpty()) throw new AuthorizationException("토큰 인증에 실패했습니다.");
    }
}
