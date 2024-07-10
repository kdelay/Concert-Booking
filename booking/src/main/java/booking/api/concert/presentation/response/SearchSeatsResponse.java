package booking.api.concert.presentation.response;

import booking.api.concert.domain.enums.ConcertSeatStatus;

import java.math.BigDecimal;

public record SearchSeatsResponse(
        int seatNumber, //좌석 번호
        BigDecimal price, //좌석 금액
        ConcertSeatStatus status //좌석 상태
) {
}