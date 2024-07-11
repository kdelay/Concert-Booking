package booking.api.waiting.domain;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class User {

    private final Long id;
    private BigDecimal amount;

    public User(Long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public static User create(Long id, BigDecimal amount) {
        return new User(id, amount);
    }

    public User chargeAmount(BigDecimal amountToAdd) {
        this.amount = this.amount.add(amountToAdd);
        return this;
    }
}
