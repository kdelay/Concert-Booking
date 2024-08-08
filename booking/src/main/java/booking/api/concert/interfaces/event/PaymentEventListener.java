package booking.api.concert.interfaces.event;

import booking.api.concert.domain.event.DataPlatformSendService;
import booking.api.concert.domain.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final DataPlatformSendService sendService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentSuccessEvent event) {
        //결제 정보 전달
        sendService.sendPaymentSuccess(event);
    }
}
