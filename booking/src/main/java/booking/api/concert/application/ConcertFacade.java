package booking.api.concert.application;

import booking.api.concert.domain.ConcertSchedule;
import booking.api.concert.domain.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;

    //예약 가능한 콘서트 날짜 조회
    public List<ConcertSchedule> searchSchedules(String token, Long concertId) {
        return concertService.searchSchedules(token, concertId);
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
    public List<List<Object>> searchSeats(String token, long concertScheduleId, LocalDate concertDate) {
        return concertService.searchSeats(token, concertScheduleId, concertDate);
    }
}
