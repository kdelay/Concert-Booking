package booking.api.concert.domain;

public interface ConcertRepository {

    Concert findByConcertId(Long concertId);
}
