package booking.api.concert.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaConcertSeatRepository extends JpaRepository<ConcertSeatEntity, Long> {
}