package booking.api.concert.domain;

import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static booking.api.concert.domain.enums.ConcertSeatStatus.AVAILABLE;
import static booking.api.concert.domain.enums.ConcertSeatStatus.TEMPORARY;
import static booking.api.concert.domain.enums.ReservationStatus.RESERVED;
import static booking.api.concert.domain.enums.ReservationStatus.RESERVING;
import static booking.api.waiting.domain.WaitingTokenStatus.EXPIRED;

@Service
@RequiredArgsConstructor
@Transactional
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final WaitingTokenRepository waitingTokenRepository;

    //10초라고 가정한다.
    private static final Duration RESERVATION_TIMEOUT = Duration.ofSeconds(10);

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

    public Reservation bookingSeats(String token, long userId, long concertScheduleId, LocalDate concertDate, List<Integer> seatNumberList) {

        //대기열 토큰 검증
        WaitingToken.tokenAuthorization(token);

        //콘서트 날짜
        ConcertSchedule concertSchedule = concertRepository.findByScheduleIdAndConcertDate(concertScheduleId, concertDate);
        Concert concert = concertSchedule.getConcert();

        //예약 좌석 리스트
        List<ConcertSeat> concertSeats = seatNumberList.stream()
                .map(seatNumber -> concertRepository.findByConcertAndScheduleAndSeatNumber(concert.getId(), concertSchedule.getId(), seatNumber))
                .toList();

        //좌석 상태 검증 및 TEMPORARY 상태로 변경
        for (ConcertSeat seat : concertSeats) {
            if (seat.getSeatStatus() != AVAILABLE) {
                throw new IllegalStateException("이미 예약되거나 선점된 좌석입니다.");
            }
            seat.updateSeatStatus(TEMPORARY);
            concertRepository.saveConcertSeat(seat);
        }

        //예약 - 좌석 임시 배정
        Reservation reservation = null;
        for (ConcertSeat seat : concertSeats) {
            reservation = concertRepository.saveReservation(
                    Reservation.create(null, seat.getId(), userId, seat.getConcert().getName(), seat.getConcertSchedule().getConcertDate(), RESERVING, LocalDateTime.now(), null)
            );
        }

        //예약 상태 변경 타임아웃 로직 추가
        if (reservation != null && Duration.between(reservation.getCreatedAt(), LocalDateTime.now()).compareTo(RESERVATION_TIMEOUT) > 0) {
            reservation.updateReservationStatus(RESERVED);
            concertRepository.saveReservation(reservation);
        } else {
            WaitingToken waitingToken = waitingTokenRepository.findByToken(token);
            waitingToken.updateWaitingTokenStatus(EXPIRED);
            waitingTokenRepository.save(waitingToken);
        }
        return reservation;
    }

    public List<BigDecimal> getConcertSeat(long userId, long concertScheduleId, LocalDate concertDate, List<Integer> seatNumberList){

        //콘서트 날짜
        ConcertSchedule concertSchedule = concertRepository.findByScheduleIdAndConcertDate(concertScheduleId, concertDate);
        Concert concert = concertSchedule.getConcert();

        List<ConcertSeat> concertSeats = seatNumberList.stream()
                .map(seatNumber -> concertRepository.findByConcertAndScheduleAndSeatNumber(concert.getId(), concertSchedule.getId(), seatNumber))
                .toList();

        List<BigDecimal> price = new ArrayList<>();
        for (ConcertSeat seat : concertSeats) {
            price.add(seat.getSeatPrice());
        }
        return price;
    }
}
