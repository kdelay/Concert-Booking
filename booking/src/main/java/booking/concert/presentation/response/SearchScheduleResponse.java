package booking.concert.presentation.response;

import java.time.LocalDate;

public record SearchScheduleResponse(
        LocalDate concertDate
) {
}
