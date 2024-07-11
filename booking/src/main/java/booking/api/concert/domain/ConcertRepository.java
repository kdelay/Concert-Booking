package booking.api.concert.domain;

import java.time.LocalDate;
import java.util.List;

public interface ConcertRepository {

    Concert findByConcertId(Long concertId);

    List<ConcertSchedule> findByConcertEntity(Concert concert);

    ConcertSchedule findByScheduleIdAndConcertDate(Long concertScheduleId, LocalDate concertDate);

    ConcertSeat findBySeatId(Long concertSeatId);

    List<ConcertSeat> findByConcertAndSchedule(Concert concert, ConcertSchedule concertSchedule);
}
