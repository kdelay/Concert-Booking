package booking.api.concert.domain;

import booking.api.concert.Payment;
import booking.api.concert.domain.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

public interface ConcertRepository {

    //concert
    Concert findByConcertId(Long concertId);

    //concert schedule
    List<ConcertSchedule> findByConcertEntity(Concert concert);

    ConcertSchedule findByScheduleIdAndConcertDate(Long concertScheduleId, LocalDate concertDate);

    //concert seat
    ConcertSeat findBySeatId(Long concertSeatId);

    List<ConcertSeat> findByConcertAndSchedule(Concert concert, ConcertSchedule concertSchedule);

    ConcertSeat findByConcertAndScheduleAndSeatNumber(Long concertId, Long concertScheduleId, int seatNumber);

    ConcertSeat saveConcertSeat(ConcertSeat concertSeat);

    //reservation
    Reservation findByReservationId(Long reservationId);

    List<Reservation> findAllByReservationStatus(ReservationStatus reservationStatus);

    Reservation saveReservation(Reservation reservation);

    //payment
    Payment findPaymentByReservation(Reservation reservation);

    Payment savePayment(Payment payment);
}
