package booking.api.concert.application;

import booking.api.concert.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;

    public List<Concert> getList() {
        return concertService.getList();
    }

    public List<ConcertSchedule> searchSchedules(Long concertId) {
        return concertService.searchSchedules(concertId);
    }

    public List<LocalDate> getConcertScheduleDates(List<ConcertSchedule> schedules) {
        return concertService.getConcertScheduleDates(schedules);
    }

    public List<Long> getConcertScheduleId(List<ConcertSchedule> schedules) {
        return concertService.getConcertScheduleId(schedules);
    }

    public List<List<Object>> searchSeats(long concertScheduleId, LocalDate concertDate) {
        return concertService.searchSeats(concertScheduleId, concertDate);
    }

    public List<Reservation> bookingSeats(long userId, long concertScheduleId, LocalDate concertDate, List<Integer> seatNumberList) {
        List<Reservation> reservations = concertService.bookingSeats(userId, concertScheduleId, concertDate, seatNumberList);
        for (Reservation reservation : reservations) {
            concertService.savePayment(reservation, reservation.getTotalAmount());
        }
        return reservations;
    }

    public void checkExpiredTimeForSeat() {
        concertService.checkExpiredTimeForSeat();
    }

    public ConcertSeat pay(Long concertSeatId, Long reservationId) {
        return concertService.pay(concertSeatId, reservationId);
    }
}
