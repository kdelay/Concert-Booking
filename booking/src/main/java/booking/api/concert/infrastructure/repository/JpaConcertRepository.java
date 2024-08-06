package booking.api.concert.infrastructure.repository;

import booking.api.concert.infrastructure.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaConcertRepository extends JpaRepository<ConcertEntity, Long> {
}
