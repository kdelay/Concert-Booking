package booking.support.scheduler;

import booking.api.waiting.domain.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingTokenScheduler {

    private final QueueService queueService;

    //매 10초마다 토큰 활성화
    @Scheduled(fixedRate = 10000)
    public void activateToken() {
        queueService.activateToken();
    }
}