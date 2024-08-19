package booking.api.concert.infrastructure.db;

import booking.api.concert.domain.PaymentOutbox;
import booking.api.concert.domain.enums.PaymentOutboxState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment_outbox")
public class PaymentOutboxEntity {

    @Id
    @Comment("식별자 PK UUID")
    private String uuid;

    @Comment("메시지")
    private String payload;

    @Comment("발행 재시도 가능 횟수")
    private int publishedCount;

    @Comment("요청 금지 상태")
    private boolean skipped;

    @Enumerated(EnumType.STRING)
    @Comment("아웃박스 상태")
    private PaymentOutboxState paymentOutboxState;

    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @Comment("변경 시간")
    private LocalDateTime modifiedAt;

    @Builder
    public PaymentOutboxEntity(
            String uuid, String payload, PaymentOutboxState paymentOutboxState,
            LocalDateTime createdAt, LocalDateTime modifiedAt
    ) {
        this.uuid = uuid;
        this.payload = payload;
        this.paymentOutboxState = paymentOutboxState;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentOutboxEntity paymentOutboxEntity = (PaymentOutboxEntity) o;
        return Objects.equals(uuid, paymentOutboxEntity.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public static PaymentOutboxEntity of(PaymentOutbox paymentOutbox) {
        return PaymentOutboxEntity.builder()
                .uuid(paymentOutbox.getUuid())
                .payload(paymentOutbox.getPayload())
                .paymentOutboxState(paymentOutbox.getPaymentOutboxState())
                .createdAt(paymentOutbox.getCreatedAt())
                .modifiedAt(paymentOutbox.getModifiedAt())
                .build();
    }

    public PaymentOutbox toDomain() {
        return PaymentOutbox.builder()
                .uuid(this.getUuid())
                .payload(this.getPayload())
                .paymentOutboxState(this.getPaymentOutboxState())
                .createdAt(this.getCreatedAt())
                .modifiedAt(this.getModifiedAt())
                .build();
    }
}
