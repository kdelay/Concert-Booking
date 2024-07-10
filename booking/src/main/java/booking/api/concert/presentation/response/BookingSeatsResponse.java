package booking.api.concert.presentation.response;

import booking.api.concert.domain.enums.ReservationStatus;

import java.math.BigDecimal;

public record BookingSeatsResponse(
        long reservationId,
        long concertSeatId,
        int seatNumber,
        BigDecimal totalPrice,
        ReservationStatus reservationStatus
) {
}
