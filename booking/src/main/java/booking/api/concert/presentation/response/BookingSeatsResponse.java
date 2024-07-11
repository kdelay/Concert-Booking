package booking.api.concert.presentation.response;

import java.math.BigDecimal;
import java.util.List;

public record BookingSeatsResponse(
        long reservationId,
        long concertSeatId,
        List<Integer> seatNumber,
        List<BigDecimal> totalPrice
) {
}
