package booking.api.concert.domain;

import booking.api.concert.domain.enums.PaymentState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Payment {

    private Long id;
    private Long reservationId;
    private BigDecimal price;
    private PaymentState paymentState;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Payment(Long id, Long reservationId, BigDecimal price, PaymentState paymentState, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.price = price;
        this.paymentState = paymentState;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Payment create(Long reservationId, BigDecimal price) {
        return new Payment(null, reservationId, price, PaymentState.PENDING, LocalDateTime.now(), null);
    }

    public void canceledPayment() {
        this.paymentState = PaymentState.CANCELED;
    }

    public void completedPayment() {
        this.paymentState = PaymentState.COMPLETED;
    }

    public void updateTime() {
        this.modifiedAt = LocalDateTime.now();
    }
}
