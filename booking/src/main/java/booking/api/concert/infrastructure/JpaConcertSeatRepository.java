package booking.api.concert.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaConcertSeatRepository extends JpaRepository<ConcertSeatEntity, Long> {

    List<ConcertSeatEntity> findByConcertEntityAndConcertScheduleEntity(ConcertEntity concertEntity, ConcertScheduleEntity concertScheduleEntity);
}