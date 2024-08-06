package booking.api.waiting.domain;

import booking.api.waiting.domain.enums.WaitingTokenStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WaitingToken {

    private final Long id;
    private final String token;
    private final WaitingTokenStatus waitingTokenStatus;
    private final Long rank;
    private final LocalDateTime createdAt;
    private final LocalDateTime entryAt;
    private LocalDateTime modifiedAt;
}
