package booking.api.concert.infrastructure;

import booking.api.concert.domain.*;
import booking.api.concert.domain.enums.ReservationStatus;
import booking.support.exception.CustomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static booking.api.concert.infrastructure.ConcertMapper.*;
import static booking.support.exception.ErrorCode.*;

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
        return toDomain(jpaConcertRepository.findById(concertId)
                .orElseThrow(() -> new CustomNotFoundException(CONCERT_IS_NOT_FOUND,
                        "해당하는 콘서트가 없습니다. [concertId : %d]".formatted(concertId)))
        );
    }

    @Override
    public List<Concert> findAllConcerts() {
        return toDomainList(jpaConcertRepository.findAll());
    }

    @Override
    public List<ConcertSchedule> findSchedulesByConcert(Concert concert) {
        return jpaConcertScheduleRepository.findByConcertEntity(toEntity(concert)).stream()
                .map(ConcertMapper::scheduleToDomain)
                .toList();
    }

    /**
     * @param concertScheduleId 콘서트 날짜 PK
     * @param concertDate 콘서트 날짜
     * @return 콘서트 날짜 정보
     */
    @Override
    public ConcertSchedule findScheduleByDate(Long concertScheduleId, LocalDate concertDate) {
        return scheduleToDomain(jpaConcertScheduleRepository.findByIdAndConcertDate(concertScheduleId, concertDate)
                .orElseThrow(() -> new CustomNotFoundException(CONCERT_SCHEDULE_IS_NOT_FOUND, "[%s] 해당하는 콘서트 날짜를 찾을 수 없습니다.".formatted(concertDate))));
    }

    @Override
    public ConcertSeat findBySeatId(Long concertSeatId) {
        return seatToDomain(
                jpaConcertSeatRepository.findById(concertSeatId)
                        .orElseThrow(() -> new CustomNotFoundException(CONCERT_SEAT_IS_NOT_FOUND, "해당하는 좌석이 없습니다."))
        );
    }

    @Override
    public List<ConcertSeat> findSeats(Concert concert, ConcertSchedule concertSchedule) {
        return seatToDomainList(
                jpaConcertSeatRepository.findByConcertEntityAndConcertScheduleEntity(
                toEntity(concert), scheduleToEntity(concertSchedule))
        );
    }

    /**
     * @param concertId 콘서트 PK
     * @param concertScheduleId  콘서트 날짜 PK
     * @param seatNumber 콘서트 좌석 번호
     * @return 콘서트 좌석 정보
     */
    @Override
    public ConcertSeat findSeatsBySeatNumber(Long concertId, Long concertScheduleId, int seatNumber) {
        return seatToDomain(
                jpaConcertSeatRepository.findSeatsBySeatNumber(
                        concertId, concertScheduleId, seatNumber)
                        .orElseThrow(() -> new CustomNotFoundException(CONCERT_SEAT_IS_NOT_FOUND, "[%d] 좌석 번호가 없습니다.".formatted(seatNumber)))
        );
    }

    @Override
    public ConcertSeat saveConcertSeat(ConcertSeat concertSeat) {
        return seatToDomain(jpaConcertSeatRepository.save(seatToEntity(concertSeat)));
    }

    @Override
    public List<ConcertSeat> saveAllSeats(List<ConcertSeat> seatList) {
        return seatToDomainList(jpaConcertSeatRepository.saveAll(seatList.stream()
                .map(ConcertMapper::seatToEntity).toList()));
    }

    @Override
    public Reservation findByReservationId(Long reservationId) {
        return reservationToDomain(jpaReservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomNotFoundException(RESERVATION_IS_NOT_FOUND, "해당하는 예약이 없습니다.")));
    }

    @Override
    public List<Reservation> findAllByReservationStatus(ReservationStatus reservationStatus) {
        return reservationToDomainList(jpaReservationRepository.findAllByReservationStatus(reservationStatus));
    }

    @Override
    public Reservation saveReservation(Reservation reservation) {
        return reservationToDomain(jpaReservationRepository.save(reservationToEntity(reservation)));
    }

    @Override
    public List<Reservation> saveAllReservations(List<Reservation> reservationList) {
        return reservationToDomainList(jpaReservationRepository.saveAll(reservationList.stream()
                .map(ConcertMapper::reservationToEntity).toList()));
    }

    @Override
    public Payment findPaymentByReservation(Long reservationId) {
        return paymentToDomain(jpaPaymentRepository.findByReservationId(reservationId));
    }

    @Override
    public Payment savePayment(Payment payment) {
        return paymentToDomain(jpaPaymentRepository.save(paymentToEntity(payment)));
    }

    @Override
    public List<Payment> saveAllPayments(List<Payment> paymentList) {
        return paymentToDomainList(jpaPaymentRepository.saveAll(paymentList.stream()
                .map(ConcertMapper::paymentToEntity).toList()));
    }
}
