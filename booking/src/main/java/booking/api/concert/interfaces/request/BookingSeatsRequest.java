package booking.api.concert.interfaces.request;

import java.time.LocalDate;
import java.util.List;

public record BookingSeatsRequest(
        long concertScheduleId, //콘서트 날짜 PK
        LocalDate concertDate, //콘서트 날짜
        List<Integer> seatNumberList, //콘서트 좌석 list
        long userId
) {
}
