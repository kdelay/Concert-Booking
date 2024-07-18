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

    /**
     * 잔액 충전
     * @param amount 충전 금액
     * @return 유저 객체
     */
    public User chargeAmount(BigDecimal amount) {
        this.amount = this.amount.add(amount);
        return this;
    }

    /**
     * 잔액 차감
     * @param amount 차감 금액
     * @return 유저 객체
     */
    public User useAmount(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
        return this;
    }
}
