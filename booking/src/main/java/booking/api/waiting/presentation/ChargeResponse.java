package booking.api.waiting.presentation;

import java.math.BigDecimal;

public record ChargeResponse(
        BigDecimal amount
) {
}
