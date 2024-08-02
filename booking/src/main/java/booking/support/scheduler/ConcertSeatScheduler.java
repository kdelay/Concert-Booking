package booking.support.scheduler;

import booking.api.concert.domain.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertSeatScheduler {

    private final ConcertService concertService;

    //매 5분마다 스케줄러 작동
    @Scheduled(fixedRate = 300000)
    public void expiredToken() {
        concertService.expiredToken();
    }
}
