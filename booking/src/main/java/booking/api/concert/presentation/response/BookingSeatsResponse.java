package booking.api.concert.presentation.response;

import booking.api.concert.domain.Reservation;

import java.util.ArrayList;
import java.util.List;

public record BookingSeatsResponse(
        long reservationId,
        long concertSeatId
) {
    public static List<BookingSeatsResponse> of(List<Reservation> reservations) {
        List<BookingSeatsResponse> result = new ArrayList<>();

        for (Reservation reservation : reservations) {
            BookingSeatsResponse response = new BookingSeatsResponse(
                    reservation.getId(),
                    reservation.getConcertSeatId()
            );
            result.add(response);
        }
        return result;
    }
}
