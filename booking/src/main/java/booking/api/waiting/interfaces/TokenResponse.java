package booking.api.waiting.interfaces;

import booking.api.waiting.domain.WaitingToken;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record TokenResponse(
        String token,
        long rank,
        String ttl
) {
    public static TokenResponse create(WaitingToken waitingToken) {
        LocalDateTime entryAt = waitingToken.getEntryAt();
        long ttl = ChronoUnit.SECONDS.between(LocalDateTime.now(), entryAt);
        String formattedTtl = String.format("%d분 %02d초", ttl / 60, ttl % 60);

        return new TokenResponse(
                waitingToken.getToken(),
                waitingToken.getRank(),
                formattedTtl
        );
    }
}
