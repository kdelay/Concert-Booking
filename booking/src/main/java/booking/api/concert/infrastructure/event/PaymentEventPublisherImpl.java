package booking.api.concert.infrastructure.event;

import booking.api.concert.domain.event.PaymentEventPublisher;
import booking.api.concert.domain.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisherImpl implements PaymentEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void success(PaymentSuccessEvent event) {
            applicationEventPublisher.publishEvent(event);
    }
}
