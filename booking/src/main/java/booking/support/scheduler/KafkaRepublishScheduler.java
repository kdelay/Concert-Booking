package booking.support.scheduler;

import booking.api.concert.domain.PaymentOutbox;
import booking.api.concert.domain.event.PaymentSuccessEvent;
import booking.api.concert.domain.message.PaymentMessage;
import booking.api.concert.domain.message.PaymentMessageOutbox;
import booking.api.concert.domain.message.PaymentMessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static booking.api.concert.domain.enums.PaymentOutboxState.INIT;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaRepublishScheduler {

    private final PaymentMessageOutbox paymentMessageOutbox;
    private final PaymentMessageSender paymentMessageSender;
    private final ObjectMapper objectMapper;

    //매 5초마다 카프카 재발행
    @Scheduled(fixedRate = 5000)
    public void republish() {
        List<PaymentOutbox> paymentOutboxes = paymentMessageOutbox.findAllByStatus(INIT);

        paymentOutboxes.stream()
                .filter(paymentOutbox -> paymentOutbox.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5)))
                .forEach(paymentOutbox -> {
                    try {
                        PaymentMessage<PaymentSuccessEvent> paymentMessage =
                                objectMapper.readValue(paymentOutbox.getPayload(),
                                        objectMapper.getTypeFactory()
                                                .constructParametricType(PaymentMessage.class, PaymentSuccessEvent.class));
                        paymentMessageSender.send(paymentMessage);
                    } catch (JsonProcessingException e) {
                        log.error("[Kafka - Scheduler] exception = {}", e.getMessage());
                    }
                });
    }
}