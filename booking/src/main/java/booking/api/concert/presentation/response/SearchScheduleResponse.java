package booking.api.concert.presentation.response;

import java.time.LocalDate;

public record SearchScheduleResponse(
        LocalDate concertDate, //콘서트 날짜
        long concertScheduleId
) {
}
