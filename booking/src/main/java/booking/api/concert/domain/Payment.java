package booking.api.concert.domain;

import booking.api.concert.domain.enums.PaymentState;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Payment {

    private Long id;
    private Reservation reservation;
    private BigDecimal price;
    private PaymentState paymentState;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Payment(Long id, Reservation reservation, BigDecimal price, PaymentState paymentState, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.reservation = reservation;
        this.price = price;
        this.paymentState = paymentState;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Payment create(Reservation reservation, BigDecimal price) {
        return new Payment(null, reservation, price, PaymentState.PENDING, LocalDateTime.now(), null);
    }

    public void updatePaymentStatus(PaymentState paymentState) {
        this.paymentState = paymentState;
    }
}
