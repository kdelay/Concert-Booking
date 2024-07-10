package booking.api.concert.presentation.request;

import booking.api.concert.domain.ConcertSeat;

import java.time.LocalDate;
import java.util.List;

public record BookingSeatsRequest(
        LocalDate concertDate, //콘서트 날짜
        List<ConcertSeat> concertSeatList, //콘서트 좌석 list
        long userId
) {
}
