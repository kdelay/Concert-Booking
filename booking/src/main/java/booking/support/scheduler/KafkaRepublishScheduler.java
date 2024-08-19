package booking.support.scheduler;

import booking.api.concert.domain.PaymentOutbox;
import booking.api.concert.domain.event.PaymentSuccessEvent;
import booking.api.concert.domain.message.PaymentMessageOutboxManager;
import booking.api.concert.domain.message.PaymentMessageSender;
import booking.support.JsonUtil;
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

    private final PaymentMessageOutboxManager paymentMessageOutboxManager;
    private final PaymentMessageSender paymentMessageSender;

    //매 5초마다 카프카 재발행
    @Scheduled(fixedRate = 5000)
    public void republishPaymentMessage() {
        List<PaymentOutbox> paymentOutboxes = paymentMessageOutboxManager.findAllByStatus(INIT);

        paymentOutboxes.stream()
                .filter(paymentOutbox -> paymentOutbox.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5)))
                .forEach(paymentOutbox -> {
                    PaymentSuccessEvent paymentMessage = JsonUtil.fromJson(paymentOutbox.getPayload(), PaymentSuccessEvent.class);
                    paymentMessageSender.send(paymentMessage);
                });
    }
}