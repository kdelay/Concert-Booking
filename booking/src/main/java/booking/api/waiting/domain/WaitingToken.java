package booking.api.waiting.domain;

import booking.support.exception.CustomNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static booking.api.waiting.domain.WaitingTokenStatus.*;
import static booking.support.exception.ErrorCode.USER_IS_NOT_FOUND;
import static booking.support.exception.ErrorCode.WAITING_TOKEN_IS_NOT_FOUND;

@Getter
public class WaitingToken {

    private final Long id;
    private Long version;
    private final User user;
    private final String token;
    private WaitingTokenStatus waitingTokenStatus;
    private final LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public WaitingToken(
            Long id,
            Long version,
            User user,
            String token,
            WaitingTokenStatus waitingTokenStatus,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {
        if (user == null || user.getId() == null) throw new CustomNotFoundException(USER_IS_NOT_FOUND, "[WaitingToken - user/userId] is null");
        if (waitingTokenStatus == null) throw new CustomNotFoundException(WAITING_TOKEN_IS_NOT_FOUND, "[WaitingToken - status] is null");
        this.id = id;
        this.version = version;
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
                0L,
                user,
                UUID.randomUUID().toString(),
                DEACTIVATE,
                LocalDateTime.now(),
                null
        );
    }

    /**
     * 대기열 입장 가능 시간 = 토큰 생성 시간 + ((대기열 순서 번호 / 분당 처리량) * 대기열 처리 시간)
     * @return 토큰 활성화 가능 여부
     */
    public boolean isEntryTime(long rank, int entryAmount, long processTime) {
        //토큰 생성 시간(ms)
        long tokenCreatedAt = this.createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        //대기열 입장 가능 시간
        long entryAccessTime = tokenCreatedAt + ((rank / entryAmount) * processTime);

        return tokenCreatedAt >= entryAccessTime;
    }

    /**
     * 대기열 토큰 상태 변경
     * @param waitingTokenStatus 대기열 토큰 상태(DEACTIVATE, ACTIVATE, EXPIRED)
     */
    public void updateStatus(WaitingTokenStatus waitingTokenStatus) {
        this.waitingTokenStatus = waitingTokenStatus;
    }

    /**
     * 토큰 상태를 ACTIVATE 로 변경
     */
    public void activateToken() {
        this.waitingTokenStatus = ACTIVATE;
    }

    /**
     * 토큰 상태를 EXPIRED 로 변경
     */
    public void expiredToken() {
        this.waitingTokenStatus = EXPIRED;
    }

    /**
     * 변경 시간 업데이트
     */
    public void updateTime() {
        this.modifiedAt = LocalDateTime.now();
    }
}
