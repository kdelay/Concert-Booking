package booking.api.concert.interfaces.response;

import java.time.LocalDate;
import java.util.List;

public record SearchScheduleResponse(
        List<LocalDate> concertDate, //콘서트 날짜
        List<Long> concertScheduleIdList
) {
}
