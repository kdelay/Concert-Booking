package booking.api.concert.domain;

import booking.api.waiting.domain.WaitingToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static booking.api.concert.domain.enums.ConcertSeatStatus.AVAILABLE;

@Service
@RequiredArgsConstructor
@Transactional
public class ConcertService {

    private final ConcertRepository concertRepository;

    public List<LocalDate> searchSchedules(String token, Long concertId) {

        //대기열 토큰 검증
        WaitingToken.tokenAuthorization(token);

        //콘서트
        Concert concert = concertRepository.findByConcertId(concertId);

        //콘서트 날짜
        List<ConcertSchedule> schedules = concertRepository.findByConcertEntity(concert);
        List<LocalDate> dates = new ArrayList<>();

        for (ConcertSchedule schedule : schedules) {
            dates.add(schedule.getConcertDate());
        }
        return dates;
    }

    public List<Long> getConcertScheduleId(Long concertId) {

        //콘서트
        Concert concert = concertRepository.findByConcertId(concertId);

        //콘서트 날짜
        List<ConcertSchedule> schedules = concertRepository.findByConcertEntity(concert);
        List<Long> idList = new ArrayList<>();
        for (ConcertSchedule schedule : schedules) {
            idList.add(schedule.getId());
        }
        return idList;
    }

    public List<List<Object>> searchSeats(String token, long concertScheduleId, LocalDate concertDate) {

        //대기열 토큰 검증
        WaitingToken.tokenAuthorization(token);

        //콘서트 날짜
        ConcertSchedule concertSchedule = concertRepository.findByScheduleIdAndConcertDate(concertScheduleId, concertDate);

        //콘서트 좌석
        List<List<Object>> seats = new ArrayList<>();
        List<ConcertSeat> concertSeats = concertRepository.findByConcertAndSchedule(concertSchedule.getConcert(), concertSchedule)
                .stream().filter(seat -> seat.getSeatStatus() == AVAILABLE).toList();
        for (ConcertSeat concertSeat : concertSeats) {
            List<Object> seatInfo = new ArrayList<>();
            seatInfo.add(concertSeat.getSeatNumber());
            seatInfo.add(concertSeat.getSeatPrice());
            seatInfo.add(concertSeat.getSeatStatus().name());
            seats.add(seatInfo);
        }
        return seats;
    }
}
