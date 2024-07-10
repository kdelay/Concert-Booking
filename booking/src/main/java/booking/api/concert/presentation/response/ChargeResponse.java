package booking.api.concert.presentation.response;

import java.math.BigDecimal;

public record ChargeResponse(
        BigDecimal amount
) {
}
