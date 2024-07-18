package booking.support.scheduler;

import booking.api.concert.application.ConcertFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertSeatScheduler {

    private final ConcertFacade concertFacade;

    //매 1분마다 스케줄러 작동
    @Scheduled(fixedRate = 60000)
    public void checkExpiredTimeForSeat() {
        concertFacade.checkExpiredTimeForSeat();
    }
}