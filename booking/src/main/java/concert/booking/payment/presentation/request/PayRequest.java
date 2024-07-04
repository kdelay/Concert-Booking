package concert.booking.payment.presentation.request;

public record PayRequest(
        Long concertSeatId,
        Long reservationId
) {
}
