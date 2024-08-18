package booking.api.concert.domain.message;

import booking.api.concert.domain.event.PaymentSuccessEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentMessage<T> {

    String messageId;
    T payLoad;

    public static PaymentMessage<PaymentSuccessEvent> create(PaymentSuccessEvent payLoad) {
        String uuid = UUID.randomUUID().toString();
        return new PaymentMessage<>(uuid, payLoad);
    }

    public static PaymentMessage<PaymentSuccessEvent> of(String uuid, PaymentSuccessEvent payLoad) {
        return new PaymentMessage<>(uuid, payLoad);
    }
}