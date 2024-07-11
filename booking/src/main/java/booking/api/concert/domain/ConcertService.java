package booking.api.concert.domain;

import booking.api.waiting.domain.WaitingToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
}
