package booking.api.concert.infrastructure.db.repository;

import booking.api.concert.infrastructure.db.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaConcertRepository extends JpaRepository<ConcertEntity, Long> {
}
