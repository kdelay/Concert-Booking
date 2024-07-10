package booking.api.concert.infrastructure;

import booking.api.concert.domain.enums.PaymentState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class PaymentEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("콘서트 결제 PK")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", referencedColumnName = "id", unique = true)
    @Comment("콘서트 예약 PK")
    private ReservationEntity reservationEntity;

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
}
