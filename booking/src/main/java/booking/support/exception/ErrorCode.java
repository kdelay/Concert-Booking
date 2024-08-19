package booking.support.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //user
    USER_IS_NOT_FOUND("유저가 존재하지 않습니다."),
    USER_AMOUNT_IS_NOT_ENOUGH("잔액이 부족합니다."),

    //waiting token
    WAITING_TOKEN_IS_NOT_FOUND("대기열 토큰이 존재하지 않습니다."),
    WAITING_TOKEN_AUTH_FAIL("토큰 인증에 실패했습니다."),
    WAITING_TOKEN_ALREADY_EXISTS("이미 존재하는 토큰입니다."),

    //concert
    CONCERT_IS_NOT_FOUND("콘서트가 존재하지 않습니다."),

    //concert schedule
    CONCERT_SCHEDULE_IS_NOT_FOUND("콘서트 날짜가 존재하지 않습니다."),

    //concert seat
    CONCERT_SEAT_ALL_RESERVED("매진되었습니다."),
    CONCERT_SEAT_IS_NOT_AVAILABLE("이미 예약되거나 임시 배정 중인 좌석입니다."),
    CONCERT_SEAT_IS_NOT_FOUND("좌석 번호가 없습니다."),

    //reservation
    RESERVATION_IS_NOT_FOUND("예약이 존재하지 않습니다"),

    //outbox
    OUTBOX_IS_NOT_FOUND("해당하는 아웃박스 데이터가 없습니다.");

    private final String msg;
}
