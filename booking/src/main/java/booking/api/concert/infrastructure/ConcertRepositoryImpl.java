package booking.api.concert.infrastructure;

import booking.api.concert.domain.Payment;
import booking.api.concert.domain.*;
import booking.api.concert.domain.enums.ReservationStatus;
import booking.common.exception.CustomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static booking.common.exception.ErrorCode.*;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final JpaConcertRepository jpaConcertRepository;
    private final JpaConcertScheduleRepository jpaConcertScheduleRepository;
    private final JpaConcertSeatRepository jpaConcertSeatRepository;
    private final JpaReservationRepository jpaReservationRepository;
    private final JpaPaymentRepository jpaPaymentRepository;

    @Override
    public Concert findByConcertId(Long concertId) {
        return ConcertMapper.toDomain(jpaConcertRepository.findById(concertId)
                .orElseThrow(() -> new CustomNotFoundException(CONCERT_IS_NOT_FOUND,
                        "해당하는 콘서트가 없습니다. [concertId : %d]".formatted(concertId)))
        );
    }

    @Override
    public List<ConcertSchedule> findByConcertEntity(Concert concert) {
        return ConcertMapper.scheduleToDomainList(
                jpaConcertScheduleRepository.findByConcertEntity(ConcertMapper.toEntity(concert))
        );
    }

    /**
     * @param concertScheduleId 콘서트 날짜 PK
     * @param concertDate 콘서트 날짜
     * @return 콘서트 날짜 정보
     */
    @Override
    public ConcertSchedule findByScheduleIdAndConcertDate(Long concertScheduleId, LocalDate concertDate) {
        return ConcertMapper.scheduleToDomain(jpaConcertScheduleRepository.findByIdAndConcertDate(concertScheduleId, concertDate)
                .orElseThrow(() -> new CustomNotFoundException(CONCERT_SCHEDULE_IS_NOT_FOUND, "[%s] 해당하는 콘서트 날짜를 찾을 수 없습니다.".formatted(concertDate))));
    }

    @Override
    public ConcertSeat findBySeatId(Long concertSeatId) {
        return ConcertMapper.seatToDomain(
                jpaConcertSeatRepository.findById(concertSeatId)
                        .orElseThrow(() -> new CustomNotFoundException(CONCERT_SEAT_IS_NOT_FOUND, "해당하는 좌석이 없습니다."))
        );
    }

    @Override
    public List<ConcertSeat> findByConcertAndSchedule(Concert concert, ConcertSchedule concertSchedule) {
        return ConcertMapper.seatToDomainList(
                jpaConcertSeatRepository.findByConcertEntityAndConcertScheduleEntity(
                ConcertMapper.toEntity(concert), ConcertMapper.scheduleToEntity(concertSchedule))
        );
    }

    /**
     * @param concertId 콘서트 PK
     * @param concertScheduleId  콘서트 날짜 PK
     * @param seatNumber 콘서트 좌석 번호
     * @return 콘서트 좌석 정보
     */
    @Override
    public ConcertSeat findByConcertAndScheduleAndSeatNumber(Long concertId, Long concertScheduleId, int seatNumber) {
        return ConcertMapper.seatToDomain(
                jpaConcertSeatRepository.findByConcertAndScheduleAndSeatNumber(
                        concertId, concertScheduleId, seatNumber)
                        .orElseThrow(() -> new CustomNotFoundException(CONCERT_SEAT_IS_NOT_FOUND, "[%d] 좌석 번호가 없습니다.".formatted(seatNumber)))
        );
    }

    @Override
    public ConcertSeat saveConcertSeat(ConcertSeat concertSeat) {
        return ConcertMapper.seatToDomain(jpaConcertSeatRepository.save(ConcertMapper.seatToEntity(concertSeat)));
    }

    @Override
    public Reservation findByReservationId(Long reservationId) {
        return ConcertMapper.reservationToDomain(jpaReservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomNotFoundException(RESERVATION_IS_NOT_FOUND, "해당하는 예약이 없습니다.")));
    }

    @Override
    public List<Reservation> findAllByReservationStatus(ReservationStatus reservationStatus) {
        return ConcertMapper.reservationToDomainList(jpaReservationRepository.findAllByReservationStatus(reservationStatus));
    }

    @Override
    public Reservation saveReservation(Reservation reservation) {
        return ConcertMapper.reservationToDomain(jpaReservationRepository.save(ConcertMapper.reservationToEntity(reservation)));
    }

    /**
     * @param reservation 예약 정보
     * @return 결제 정보
     */
    @Override
    public Payment findPaymentByReservation(Reservation reservation) {
        return ConcertMapper.paymentToDomain(jpaPaymentRepository.findByReservationEntity(ConcertMapper.reservationToEntity(reservation)));
    }

    @Override
    public Payment savePayment(Payment payment) {
        return ConcertMapper.paymentToDomain(jpaPaymentRepository.save(ConcertMapper.paymentToEntity(payment)));
    }
}
