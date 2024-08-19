package booking.api.concert.domain;

import booking.api.concert.domain.enums.PaymentOutboxState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentOutbox {

    private final String uuid;
    private final String payload;
    private int publishedCount;
    private boolean skipped;
    private PaymentOutboxState paymentOutboxState;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public PaymentOutbox(
            String uuid, String payload, int publishedCount, boolean skipped,
            PaymentOutboxState paymentOutboxState, LocalDateTime createdAt, LocalDateTime modifiedAt
    ) {
        this.uuid = uuid;
        this.payload = payload;
        this.publishedCount = publishedCount;
        this.skipped = skipped;
        this.paymentOutboxState = paymentOutboxState;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public void publish() {
        this.paymentOutboxState = PaymentOutboxState.PUBLISHED;
    }

    public void updateTime() {
        this.modifiedAt = LocalDateTime.now();
    }


    public static PaymentOutbox create(String uuid, String payload) {
        return PaymentOutbox.builder()
                .uuid(uuid)
                .payload(payload)
                .publishedCount(0)
                .skipped(false)
                .paymentOutboxState(PaymentOutboxState.INIT)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void setTenMinAgo() {
        this.createdAt = LocalDateTime.now().minusMinutes(10);
    }
}
