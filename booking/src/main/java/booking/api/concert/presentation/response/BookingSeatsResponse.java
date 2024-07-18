package booking.api.concert.presentation.response;

import booking.api.concert.domain.Reservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record BookingSeatsResponse(
        long concertSeatId,
        String concertName,
        LocalDate concertDate
) {
    public static List<BookingSeatsResponse> of(List<Reservation> reservations) {
        List<BookingSeatsResponse> result = new ArrayList<>();

        for (Reservation reservation : reservations) {
            result.add(new BookingSeatsResponse(reservation.getConcertSeatId(),
                    reservation.getConcertName(), reservation.getConcertDate()));
        }
        return result;
    }
}
