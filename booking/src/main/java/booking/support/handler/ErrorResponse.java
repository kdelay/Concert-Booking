package booking.support.handler;

public record ErrorResponse(
        String code,
        String message
) {
}
