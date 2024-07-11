package booking.api.concert.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JpaConcertScheduleRepository extends JpaRepository<ConcertScheduleEntity, Long> {

    List<ConcertScheduleEntity> findByConcertEntity(ConcertEntity concertEntity);

    ConcertScheduleEntity findByIdAndConcertDate(Long concertScheduleId, LocalDate concertDate);
}
