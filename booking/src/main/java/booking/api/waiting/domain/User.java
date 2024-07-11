package booking.api.waiting.domain;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class User {

    private final Long id;
    private final BigDecimal amount;

    public User(Long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public static User create(Long id, BigDecimal amount) {
        return new User(id, amount);
    }
}
