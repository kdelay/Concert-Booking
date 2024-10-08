package booking.api.concert.infrastructure.db.repository;

import booking.api.concert.infrastructure.db.ConcertEntity;
import booking.api.concert.infrastructure.db.ConcertScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JpaConcertScheduleRepository extends JpaRepository<ConcertScheduleEntity, Long> {

    List<ConcertScheduleEntity> findByConcertEntity(ConcertEntity concertEntity);

    Optional<ConcertScheduleEntity> findByIdAndConcertDate(Long id, LocalDate concertDate);
}
