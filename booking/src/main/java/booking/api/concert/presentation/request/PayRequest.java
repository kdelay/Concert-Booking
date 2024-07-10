package booking.api.concert.presentation.request;

public record PayRequest(
        Long concertSeatId,
        Long reservationId
) {
}
