package booking.concert.domain.repository;

import booking.concert.infrastructure.entity.ConcertEntity;

import java.util.Optional;

public interface ConcertRepository {

    Optional<ConcertEntity> findById(Long concertId);
}
