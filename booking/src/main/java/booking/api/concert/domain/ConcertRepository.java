package booking.api.concert.domain;

import booking.api.concert.Payment;

import java.time.LocalDate;
import java.util.List;

public interface ConcertRepository {

    Concert findByConcertId(Long concertId);

    List<ConcertSchedule> findByConcertEntity(Concert concert);

    ConcertSchedule findByScheduleIdAndConcertDate(Long concertScheduleId, LocalDate concertDate);

    ConcertSeat findBySeatId(Long concertSeatId);

    List<ConcertSeat> findByConcertAndSchedule(Concert concert, ConcertSchedule concertSchedule);

    ConcertSeat findByConcertAndScheduleAndSeatNumber(Long concertId, Long concertScheduleId, int seatNumber);

    ConcertSeat saveConcertSeat(ConcertSeat concertSeat);

    Reservation findByReservationId(Long reservationId);

    Reservation saveReservation(Reservation reservation);

    Payment savePayment(Payment payment);
}
