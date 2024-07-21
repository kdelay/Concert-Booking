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

    //콘서트 조회
    public List<Concert> getList() {
        return concertService.getList();
    }

    //예약 가능한 콘서트 날짜 조회
    public List<ConcertSchedule> searchSchedules(Long concertId) {
        return concertService.searchSchedules(concertId);
    }

    //콘서트 날짜 정보
    public List<LocalDate> getConcertScheduleDates(List<ConcertSchedule> schedules) {
        return concertService.getConcertScheduleDates(schedules);
    }

    //콘서트 날짜 PK 정보
    public List<Long> getConcertScheduleId(List<ConcertSchedule> schedules) {
        return concertService.getConcertScheduleId(schedules);
    }

    //콘서트 날짜와 일치하는 예약 가능한 좌석 조회
    public List<List<Object>> searchSeats(long concertScheduleId, LocalDate concertDate) {
        return concertService.searchSeats(concertScheduleId, concertDate);
    }

    //콘서트 좌석 예약 및 결제 정보 저장
    public List<Reservation> bookingSeats(long userId, long concertScheduleId, LocalDate concertDate, List<Integer> seatNumberList) {
        List<Reservation> reservations = concertService.bookingSeats(userId, concertScheduleId, concertDate, seatNumberList);
        for (Reservation reservation : reservations) {
            concertService.savePayment(reservation, reservation.getTotalAmount());
        }
        return reservations;
    }

    //콘서트 좌석 임시 배정 시간 및 예약 만료 시간 체크
    public void checkExpiredTimeForSeat() {
        concertService.checkExpiredTimeForSeat();
    }

    //결제
    public ConcertSeat pay(Long concertSeatId, Long reservationId) {
        return concertService.pay(concertSeatId, reservationId);
    }
}
