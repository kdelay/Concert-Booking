package booking.payment.presentation.request;

public record PayRequest(
        Long concertSeatId,
        Long reservationId
) {
}
