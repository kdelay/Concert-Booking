package booking.concert.domain.service;

import booking.concert.domain.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, rollbackFor = {Exception.class})
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    public void selectSchedule(Long concertId) {
        concertRepository.findById(concertId);

    }
}