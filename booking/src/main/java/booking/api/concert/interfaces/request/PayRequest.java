package booking.api.concert.interfaces.request;

public record PayRequest(
        Long concertSeatId,
        Long reservationId
) {
}
