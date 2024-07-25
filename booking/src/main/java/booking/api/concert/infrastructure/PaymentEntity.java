package booking.api.concert.infrastructure;

import booking.api.concert.domain.enums.PaymentState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class PaymentEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("콘서트 결제 PK")
    private Long id;

    @Column(nullable = false)
    @Comment("콘서트 예약 PK")
    private Long reservationId;

    @Column(precision = 7, scale = 0) //1,000,000 단위
    @Comment("결제 금액")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Comment("결제 상태")
    private PaymentState paymentState;

    @Comment("콘서트 결제 요청 시간")
    private LocalDateTime createdAt;

    @Comment("상태 변경 시간")
    private LocalDateTime modifiedAt;

    @Builder
    public PaymentEntity(
        Long id,
        Long reservationId,
        BigDecimal price,
        PaymentState paymentState,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.reservationId = reservationId;
        this.price = price;
        this.paymentState = paymentState;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentEntity paymentEntity = (PaymentEntity) o;
        return Objects.equals(id, paymentEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
