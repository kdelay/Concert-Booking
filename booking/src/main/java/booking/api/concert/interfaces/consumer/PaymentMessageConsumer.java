package booking.api.concert.interfaces.consumer;

import booking.api.concert.domain.event.PaymentSuccessEvent;
import booking.api.concert.domain.message.PaymentMessageOutboxManager;
import booking.support.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageConsumer {

    private final PaymentMessageOutboxManager paymentMessageOutboxManager;

    @KafkaListener(topics = "${payment.topic-name}", groupId = "${spring.application.name}")
    void complete(String message) {
        try {
            PaymentSuccessEvent paymentMessage = JsonUtil.fromJson(message, PaymentSuccessEvent.class);
            log.info("[Kafka - Consume] message consume success = {}", paymentMessage);
            paymentMessageOutboxManager.complete(paymentMessage);
        } catch (Exception e) {
            log.error("Failed to parse message", e);
        }
    }
}