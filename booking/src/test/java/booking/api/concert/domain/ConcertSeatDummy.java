package booking.api.concert.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static booking.api.concert.domain.enums.ConcertSeatStatus.AVAILABLE;

public class ConcertSeatDummy {

    public static List<ConcertSeat> getConcertSeatList(Concert concert, List<ConcertSchedule> schedules) {

        List<ConcertSeat> list = new ArrayList<>();

        for (ConcertSchedule schedule : schedules) {
            for (int i = 1; i <= 50; i++) {
                list.add(ConcertSeat.create((long) i, concert, schedule, null, i, BigDecimal.valueOf(1000), AVAILABLE, LocalDateTime.now(), null));
            }
        }
        return list;
    }
}
