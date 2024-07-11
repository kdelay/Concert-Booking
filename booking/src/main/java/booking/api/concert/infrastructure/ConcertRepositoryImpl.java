package booking.api.concert.infrastructure;

import booking.api.concert.domain.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final JpaConcertRepository jpaConcertRepository;
    private final JpaConcertScheduleRepository jpaConcertScheduleRepository;
    private final JpaConcertSeatRepository jpaConcertSeatRepository;
    private final JpaReservationRepository jpaReservationRepository;

    @Override
    public Concert findByConcertId(Long concertId) {
        return ConcertMapper.toDomain(jpaConcertRepository.findById(concertId).orElseThrow(() -> new EntityNotFoundException("해당하는 콘서트가 없습니다.")));
    }

    @Override
    public List<ConcertSchedule> findByConcertEntity(Concert concert) {
        return ConcertMapper.scheduleToDomainList(
                jpaConcertScheduleRepository.findByConcertEntity(ConcertMapper.toEntity(concert))
        );
    }
    @Override
    public ConcertSchedule findByScheduleIdAndConcertDate(Long concertScheduleId, LocalDate concertDate) {
        return ConcertMapper.scheduleToDomain(jpaConcertScheduleRepository.findByIdAndConcertDate(concertScheduleId, concertDate));
    }

    @Override
    public ConcertSeat findBySeatId(Long concertSeatId) {
        return ConcertMapper.seatToDomain(
                jpaConcertSeatRepository.findById(concertSeatId).orElseThrow(() -> new EntityNotFoundException("해당하는 좌석이 없습니다."))
        );
    }

    @Override
    public List<ConcertSeat> findByConcertAndSchedule(Concert concert, ConcertSchedule concertSchedule) {
        return ConcertMapper.seatToDomainList(
                jpaConcertSeatRepository.findByConcertEntityAndConcertScheduleEntity(
                ConcertMapper.toEntity(concert), ConcertMapper.scheduleToEntity(concertSchedule))
        );
    }

    @Override
    public ConcertSeat findByConcertAndScheduleAndSeatNumber(Long concertId, Long concertScheduleId, int seatNumber) {
        return ConcertMapper.seatToDomain(
                jpaConcertSeatRepository.findByConcertAndScheduleAndSeatNumber(
                        concertId, concertScheduleId, seatNumber)
        );
    }

    @Override
    public ConcertSeat saveConcertSeat(ConcertSeat concertSeat) {
        return ConcertMapper.seatToDomain(jpaConcertSeatRepository.save(ConcertMapper.seatToEntity(concertSeat)));
    }

    @Override
    public Reservation saveReservation(Reservation reservation) {
        return jpaReservationRepository.save(reservation);
    }
}
