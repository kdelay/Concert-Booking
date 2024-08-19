package booking.api.concert.domain.message;

import booking.api.concert.domain.PaymentOutbox;
import booking.api.concert.domain.enums.PaymentOutboxState;
import booking.api.concert.domain.event.PaymentSuccessEvent;

import java.util.List;

public interface PaymentMessageOutboxManager {

    PaymentOutbox save(PaymentSuccessEvent message);

    void complete(PaymentSuccessEvent message);

    PaymentOutbox findByUuid(String uuid);

    List<PaymentOutbox> findAll();

    List<PaymentOutbox> findAllByStatus(PaymentOutboxState paymentOutboxState);

    PaymentOutbox entitySave(PaymentOutbox paymentOutbox);
}
