package booking.api.concert.interfaces.consumer;

import booking.api.concert.domain.event.PaymentSuccessEvent;
import booking.api.concert.domain.message.PaymentMessage;
import booking.api.concert.domain.message.PaymentMessageOutbox;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentMessageOutbox paymentMessageOutbox;

    @KafkaListener(topics = "${payment.topic-name}", groupId = "${spring.application.name}")
    void complete(String message) {
        try {
            PaymentMessage<PaymentSuccessEvent> paymentMessage =
                    objectMapper.readValue(message, objectMapper.getTypeFactory()
                            .constructParametricType(PaymentMessage.class, PaymentSuccessEvent.class));
            log.info("[Kafka - Consume] message consume success = {}", paymentMessage);
            paymentMessageOutbox.complete(paymentMessage);
        } catch (Exception e) {
            log.error("Failed to parse message", e);
        }
    }
}