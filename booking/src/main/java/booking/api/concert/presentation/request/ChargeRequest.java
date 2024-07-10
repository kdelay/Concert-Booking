package booking.api.concert.presentation.request;

import java.math.BigDecimal;

public record ChargeRequest(
        BigDecimal amount
) {
}
