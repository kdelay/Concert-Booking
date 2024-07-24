package booking.support.scheduler;

import booking.api.waiting.domain.WaitingTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingTokenScheduler {

    private final WaitingTokenService waitingTokenService;

    //매 1분마다 스케줄러 작동
    @Scheduled(fixedRate = 60000)
    public void checkEntryAndUpdateState() {
        waitingTokenService.getDeactivateTokens()
                .forEach(waitingTokenService::activateTokens);
    }
}