package booking.api.concert.infrastructure;

import booking.api.concert.domain.MockApiClient;
import booking.api.concert.domain.Payment;
import booking.api.concert.domain.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockApiClientImpl implements MockApiClient {

    @Override
    public boolean sendSlack(Reservation reservation, Payment payment) {
        try {
            log.info("[SUCCESS] Send info to Slack");
        } catch (RuntimeException e) {
            throw new RuntimeException("[FAIL] Send info to Slack", e);
        }
        return true;
    }
}
