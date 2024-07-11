package booking.api.concert.infrastructure;

import booking.api.concert.domain.Concert;
import booking.api.concert.domain.ConcertRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final JpaConcertRepository jpaConcertRepository;

    @Override
    public Concert findByConcertId(Long concertId) {
        return ConcertMapper.toDomain(jpaConcertRepository.findById(concertId).orElseThrow(EntityNotFoundException::new));
    }
}
