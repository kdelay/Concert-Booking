package booking.api.waiting.domain;

import booking.common.exception.AuthorizationException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import static booking.api.waiting.domain.WaitingTokenStatus.ACTIVATE;

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
        if (user == null || user.getId() == null) throw new IllegalArgumentException("[WaitingToken - user/userId] is null");
        if (waitingTokenStatus == null) throw new IllegalArgumentException("[WaitingToken - status] is null");
        this.id = id;
        this.user = user;
        this.token = (token == null || token.isEmpty()) ? UUID.randomUUID().toString() : token;
        this.waitingTokenStatus = waitingTokenStatus;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.modifiedAt = modifiedAt == null ? LocalDateTime.now() : modifiedAt;
    }

    public WaitingToken(User user, WaitingTokenStatus waitingTokenStatus) {
        if (user == null || user.getId() == null) throw new IllegalArgumentException("[WaitingToken - user/userId] is null");
        if (waitingTokenStatus == null) throw new IllegalArgumentException("[WaitingToken - status] is null");

        this.id = null;
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.waitingTokenStatus = waitingTokenStatus;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public static WaitingToken create(
            Long id,
            User user,
            String token,
            WaitingTokenStatus waitingTokenStatus,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {
        return new WaitingToken(id, user, token, waitingTokenStatus, createdAt, modifiedAt);
    }

    public void updateTokenActivate() {
        this.waitingTokenStatus = ACTIVATE;
    }

    public static void tokenAuthorization(String token) {
        if (token == null || token.isEmpty()) throw new AuthorizationException("토큰 인증에 실패했습니다.");
    }

    public void updateWaitingTokenStatus(WaitingTokenStatus waitingTokenStatus) {
        this.waitingTokenStatus = waitingTokenStatus;
    }
}
