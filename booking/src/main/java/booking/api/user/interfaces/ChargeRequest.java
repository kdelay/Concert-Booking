package booking.api.user.interfaces;

import java.math.BigDecimal;

public record ChargeRequest(
        BigDecimal amount
) {
}
