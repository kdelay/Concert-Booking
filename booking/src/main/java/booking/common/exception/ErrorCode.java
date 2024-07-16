package booking.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //user
    USER_IS_NOT_FOUND("유저가 존재하지 않습니다."),

    //waiting token
    WAITING_TOKEN_IS_NOT_FOUND("대기열 토큰이 존재하지 않습니다."),

    //concert
    CONCERT_IS_NOT_FOUND("콘서트가 존재하지 않습니다.");

    private final String msg;
}
