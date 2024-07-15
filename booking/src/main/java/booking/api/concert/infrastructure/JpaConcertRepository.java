package booking.api.concert.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaConcertRepository extends JpaRepository<ConcertEntity, Long> {
}
