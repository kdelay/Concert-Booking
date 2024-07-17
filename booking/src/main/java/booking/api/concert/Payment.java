package booking.api.concert;

import booking.api.concert.domain.Reservation;
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

    /**
     * 새로운 결제 데이터 생성
     * @param reservation 예약 정보
     * @param price 결제 금액
     * @return 결제 정보
     */
    public static Payment create(Reservation reservation, BigDecimal price) {
        return new Payment(null, reservation, price, PaymentState.PENDING, LocalDateTime.now(), null);
    }

    /**
     * 결제 상태 변경
     * @param paymentState 결제 상태
     */
    public void updatePaymentStatus(PaymentState paymentState) {
        this.paymentState = paymentState;
    }
}
