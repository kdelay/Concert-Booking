package booking.api.concert.domain;

import java.util.List;

public interface ConcertRepository {

    Concert findByConcertId(Long concertId);

    List<ConcertSchedule> findByConcertEntity(Concert concert);

    ConcertSeat findBySeatId(Long concertSeatId);
}
