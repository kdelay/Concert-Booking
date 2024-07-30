package booking.dummy;

import booking.api.concert.domain.Concert;
import booking.api.concert.domain.ConcertSchedule;
import booking.api.concert.domain.ConcertSeat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static booking.api.concert.domain.enums.ConcertSeatStatus.*;

public class ConcertSeatDummy {

    public static List<ConcertSeat> getConcertSeatList(Concert concert, List<ConcertSchedule> schedules) {

        List<ConcertSeat> list = new ArrayList<>();

        for (ConcertSchedule schedule : schedules) {
            for (int i = 1; i <= 20; i++) {
                //1~20 번 좌석 예약 가능
                list.add(new ConcertSeat((long) i, 0L, concert, schedule, null, i,
                        BigDecimal.valueOf(1000), AVAILABLE, LocalDateTime.now(), null));
            }
            for (int i = 21; i <= 40; i++) {
                //21~40 번 좌석 임시 예약 상태
                list.add(new ConcertSeat((long) i, 0L, concert, schedule, 1L, i,
                        BigDecimal.valueOf(1000), TEMPORARY, LocalDateTime.now(), null));
            }
            for (int i = 41; i <= 50; i++) {
                //41~50 번 이미 좌석 예약됨
                list.add(new ConcertSeat((long) i, 0L, concert, schedule, 1L, i,
                        BigDecimal.valueOf(1000), RESERVED, LocalDateTime.now(), null));
            }
        }
        return list;
    }

    public static List<ConcertSeat> getAllSeatReserved(Concert concert, List<ConcertSchedule> schedules) {
        List<ConcertSeat> list = new ArrayList<>();

        for (ConcertSchedule schedule : schedules) {
            for (int i = 1; i <= 50; i++) {
                //1~50 번 이미 좌석 예약됨
                list.add(new ConcertSeat((long) i, 0L, concert, schedule, 1L, i,
                        BigDecimal.valueOf(1000), RESERVED, LocalDateTime.now(), null));
            }
        }
        return list;
    }
}
