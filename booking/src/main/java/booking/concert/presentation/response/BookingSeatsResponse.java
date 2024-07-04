package booking.concert.presentation.response;

import booking.reservation.domain.ReservationStatus;

public record BookingSeatsResponse(
        ReservationStatus reservationStatus
) {
}
