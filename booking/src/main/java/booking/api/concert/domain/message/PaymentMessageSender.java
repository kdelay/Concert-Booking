package booking.api.concert.domain.message;

import booking.api.concert.domain.event.PaymentSuccessEvent;

public interface PaymentMessageSender {

    void send(PaymentMessage<PaymentSuccessEvent> message);
}
