package booking.api.concert.domain;

import booking.api.concert.domain.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

public interface ConcertRepository {

    //concert
    Concert findByConcertId(Long concertId);

    List<Concert> findAllConcerts();

    //------------------------------------------------------------------------------------------------

    //concert schedule
    List<ConcertSchedule> findSchedulesByConcert(Concert concert);

    ConcertSchedule findScheduleByDate(Long concertScheduleId, LocalDate concertDate);

    //------------------------------------------------------------------------------------------------

    //concert seat
    ConcertSeat findBySeatId(Long concertSeatId);

    List<ConcertSeat> findSeats(Concert concert, ConcertSchedule concertSchedule);

    ConcertSeat findSeatsBySeatNumber(Long concertId, Long concertScheduleId, int seatNumber);

    ConcertSeat saveConcertSeat(ConcertSeat concertSeat);

    List<ConcertSeat> saveAllSeats(List<ConcertSeat> seatList);

    //------------------------------------------------------------------------------------------------

    //reservation
    Reservation findByReservationId(Long reservationId);

    List<Reservation> findAllByReservationStatus(ReservationStatus reservationStatus);

    Reservation saveReservation(Reservation reservation);

    List<Reservation> saveAllReservations(List<Reservation> reservationList);

    //------------------------------------------------------------------------------------------------

    //payment
    Payment findPaymentByReservation(Long reservationId);

    Payment savePayment(Payment payment);

    List<Payment> saveAllPayments(List<Payment> paymentList);
}
