package booking.support.scheduler;

import booking.api.waiting.application.WaitingTokenFacade;
import booking.api.waiting.domain.WaitingToken;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WaitingTokenScheduler {

    private final WaitingTokenFacade waitingTokenFacade;

    //매 1분마다 스케줄러 작동
    @Scheduled(fixedRate = 60000)
    public void checkEntryAndUpdateState() {
        List<WaitingToken> waitingTokens = waitingTokenFacade.getDeactivateTokens();
        for (WaitingToken waitingToken : waitingTokens) {
            waitingTokenFacade.checkEntryAndUpdateState(waitingToken);
        }
    }
}
