package booking.common.handler;

public record ErrorResponse(
        String code,
        String message
) {
}
