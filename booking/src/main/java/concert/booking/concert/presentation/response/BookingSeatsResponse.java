package concert.booking.concert.presentation.response;

import concert.booking.reservation.domain.ReservationStatus;

public record BookingSeatsResponse(
        ReservationStatus reservationStatus
) {
}
