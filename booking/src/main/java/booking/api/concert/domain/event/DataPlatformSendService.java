package booking.api.concert.domain.event;

import booking.api.concert.domain.MockApiClient;
import booking.api.waiting.domain.WaitingTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataPlatformSendService {

    private final WaitingTokenRepository waitingTokenRepository;
    private final MockApiClient mockApiClient;

    public void sendPaymentSuccess(PaymentSuccessEvent event) {
        //token 만료
        waitingTokenRepository.expireToken(event.getToken());

        //slack API 예약 및 결제 정보 전송
        mockApiClient.sendSlack(event.getReservation(), event.getPayment());
    }
}
