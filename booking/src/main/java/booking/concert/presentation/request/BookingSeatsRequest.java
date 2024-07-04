package booking.concert.presentation.request;

import java.time.LocalDate;

public record BookingSeatsRequest(
        LocalDate concertDate,
        Long concertSeatId
) {
}
