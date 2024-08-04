package booking.api.waiting.presentation;

public record TokenResponse(
        String token,
        long rank,
        String ttl
) {
}
