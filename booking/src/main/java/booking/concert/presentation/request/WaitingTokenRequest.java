package booking.concert.presentation.request;

public record WaitingTokenRequest(
        Long userId,
        Long concertId
) {
}
