package booking.api.concert.domain.message;

import booking.api.concert.domain.PaymentOutbox;
import booking.api.concert.domain.enums.PaymentOutboxState;
import booking.api.concert.domain.event.PaymentSuccessEvent;

import java.util.List;

public interface PaymentMessageOutbox {

    PaymentOutbox save(PaymentMessage<PaymentSuccessEvent> message);

    void complete(PaymentMessage<PaymentSuccessEvent> message);

    PaymentOutbox findByUuid(String uuid);

    List<PaymentOutbox> findAll();

    List<PaymentOutbox> findAllByStatus(PaymentOutboxState paymentOutboxState);

    PaymentOutbox entitySave(PaymentOutbox paymentOutbox);
}
