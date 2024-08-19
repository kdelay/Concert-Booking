package booking.api.concert.infrastructure.kafka;

import booking.api.concert.domain.event.PaymentSuccessEvent;
import booking.api.concert.domain.message.PaymentMessageSender;
import booking.support.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaMessageSender implements PaymentMessageSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${payment.topic-name}")
    private String paymentTopic;

    @Override
    public void send(PaymentSuccessEvent message) {
        String payload = JsonUtil.toJson(message);
        kafkaTemplate.send(paymentTopic, payload).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("[Kafka - Send] Failed to send payload. Error: {}", exception.getMessage(), exception);
            } else {
                log.info("[Kafka - Send] Payload sent successfully. Payload: {}", payload);
            }
        });
    }
}