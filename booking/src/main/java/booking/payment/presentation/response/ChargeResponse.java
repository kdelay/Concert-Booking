package booking.payment.presentation.response;

public record ChargeResponse(
        Long userId,
        int payment
) {
}
