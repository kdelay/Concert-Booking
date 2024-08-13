package booking.api.concert.domain.event;

public interface PaymentEventPublisher {

    void success(PaymentSuccessEvent event);
}