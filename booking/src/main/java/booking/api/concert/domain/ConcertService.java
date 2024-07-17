package booking.api.concert.domain;

import booking.api.concert.Payment;
import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenRepository;
import booking.common.exception.CustomBadRequestException;
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
import static booking.api.concert.domain.enums.PaymentState.COMPLETED;
import static booking.api.concert.domain.enums.ReservationStatus.RESERVED;
import static booking.api.concert.domain.enums.ReservationStatus.RESERVING;
import static booking.api.waiting.domain.WaitingTokenStatus.EXPIRED;
import static booking.common.exception.ErrorCode.CONCERT_SEAT_ALL_RESERVED;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final WaitingTokenRepository waitingTokenRepository;

    //1분이라고 가정한다.
    private static final Duration RESERVATION_TIMEOUT = Duration.ofSeconds(60);

    /**
     * 예약 가능한 콘서트 날짜 조회
     * @param token Auth - Bearer Token
     * @param concertId 콘서트 PK
     * @return 콘서트 날짜 정보 List : ConcertSchedule
     */
    @Transactional(readOnly = true, rollbackFor = {Exception.class})
    public List<ConcertSchedule> searchSchedules(String token, Long concertId) {

        //대기열 토큰 검증
        WaitingToken.tokenAuthorization(token);

        //콘서트 정보 조회
        Concert concert = concertRepository.findByConcertId(concertId);

        //콘서트 날짜 정보 조회
        return concertRepository.findByConcertEntity(concert);
    }

    /**
     * @param schedules 콘서트 날짜 정보
     * @return 콘서트 날짜 리스트 배열 반환
     */
    public List<LocalDate> getConcertScheduleDates(List<ConcertSchedule> schedules) {
        List<LocalDate> dates = new ArrayList<>();
        for (ConcertSchedule schedule : schedules) {
            dates.add(schedule.getConcertDate());
        }
        return dates;
    }

    /**
     * @param schedules 콘서트 날짜 정보
     * @return 콘서트 날짜 PK 리스트 배열 반환
     */
    public List<Long> getConcertScheduleId(List<ConcertSchedule> schedules) {
        List<Long> idList = new ArrayList<>();
        for (ConcertSchedule schedule : schedules) {
            idList.add(schedule.getId());
        }
        return idList;
    }

    /**
     * 콘서트 날짜와 일치하는 예약 가능한 좌석 조회
     * @param token Auth - Bearer Token
     * @param concertScheduleId 콘서트 날짜 PK
     * @param concertDate 콘서트 날짜
     * @return 콘서트 좌석 정보 List : seatNumber, seatPrice, seatStatus
     */
    @Transactional(readOnly = true, rollbackFor = {Exception.class})
    public List<List<Object>> searchSeats(String token, long concertScheduleId, LocalDate concertDate) {

        //대기열 토큰 검증
        WaitingToken.tokenAuthorization(token);

        //날짜와 일치하는 콘서트 날짜 정보 조회
        ConcertSchedule concertSchedule = concertRepository.findByScheduleIdAndConcertDate(concertScheduleId, concertDate);

        //예약 가능한 콘서트 좌석 조회 후 좌석 정보 반환
        List<List<Object>> seats = new ArrayList<>();

        List<ConcertSeat> concertSeats = concertRepository.findByConcertAndSchedule(concertSchedule.getConcert(), concertSchedule)
                .stream().filter(seat -> seat.getSeatStatus() == AVAILABLE).toList();

        //예약 가능한 콘서트 좌석이 없는 경우 예약할 수 없다.
        if (concertSeats.isEmpty()) throw new CustomBadRequestException(CONCERT_SEAT_ALL_RESERVED, "매진되었습니다.");

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

    public ConcertSeat pay(String token, Long concertSeatId, Long reservationId) {

        //대기열 토큰 검증
        WaitingToken.tokenAuthorization(token);

        //예약 정보 가져오기
        Reservation reservation = concertRepository.findByReservationId(reservationId);

        //예약 만료 시간 확인
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = reservation.getCreatedAt().plusSeconds(10); //예약 만료 시간(예시: 10초)

        if (now.isAfter(expiryTime)) {
            throw new RuntimeException("Reservation expired");
        }

        //유저 잔액 확인 및 결제 처리
        Long userId = reservation.getUserId();
        User user = waitingTokenRepository.findByUserId(userId);
        BigDecimal totalAmount = reservation.getTotalAmount();
        BigDecimal userAmount = user.getAmount();

        if (userAmount.compareTo(totalAmount) < 0) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }

        //결제 처리
        user.chargeAmount(totalAmount.negate()); //유저 잔액에서 결제 금액 차감
        waitingTokenRepository.saveUser(user);

        //Payment 추가
        Payment payment = Payment.create(null, reservation, totalAmount, COMPLETED, LocalDateTime.now(), LocalDateTime.now());
        concertRepository.savePayment(payment);

        //ConcertSeat 상태 변경
        ConcertSeat concertSeat = concertRepository.findBySeatId(concertSeatId);
        concertSeat.updateSeatStatus(ConcertSeatStatus.RESERVED); //예약된 상태로 변경
        concertRepository.saveConcertSeat(concertSeat);

        //Reservation 상태 변경
        reservation.updateReservationStatus(RESERVED); //예약된 상태로 변경
        concertRepository.saveReservation(reservation);

        return concertSeat;
    }
}
