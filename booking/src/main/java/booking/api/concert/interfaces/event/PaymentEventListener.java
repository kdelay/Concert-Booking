package booking.api.concert.interfaces.event;

import booking.api.concert.application.DataPlatformSendService;
import booking.api.concert.domain.PaymentOutbox;
import booking.api.concert.domain.event.PaymentSuccessEvent;
import booking.api.concert.domain.message.PaymentMessage;
import booking.api.concert.domain.message.PaymentMessageOutbox;
import booking.api.concert.domain.message.PaymentMessageSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);

    private final DataPlatformSendService sendService;
    private final PaymentMessageOutbox paymentMessageOutbox;
    private final PaymentMessageSender paymentMessageSender;

    private PaymentOutbox paymentOutbox = null;

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    void createOutboxMessage(PaymentSuccessEvent event) {
        paymentOutbox = paymentMessageOutbox.save(PaymentMessage.create(event));
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    void sendMessage(PaymentSuccessEvent event) {
        logger.info("[Kafka - Listener] event send success = {}", event);
        String uuid = paymentOutbox.getUuid();
        paymentMessageSender.send(PaymentMessage.of(uuid, event));
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void expireToken(PaymentSuccessEvent event) {
        sendService.expireToken(event.getToken());
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void sendSlack(PaymentSuccessEvent event) {
        sendService.sendSlack(event.getReservation(), event.getPayment());
    }
}