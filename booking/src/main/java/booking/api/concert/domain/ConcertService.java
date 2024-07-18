package booking.api.concert.domain;

import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.concert.domain.enums.PaymentState;
import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenRepository;
import booking.api.waiting.domain.WaitingTokenStatus;
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
import static booking.api.concert.domain.enums.ReservationStatus.*;
import static booking.common.exception.ErrorCode.CONCERT_SEAT_ALL_RESERVED;
import static booking.common.exception.ErrorCode.CONCERT_SEAT_IS_NOT_AVAILABLE;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final WaitingTokenRepository waitingTokenRepository;

    /**
     * 예약 가능한 콘서트 날짜 조회
     * @param concertId 콘서트 PK
     * @return 콘서트 날짜 정보 List : ConcertSchedule
     */
    @Transactional(readOnly = true, rollbackFor = {Exception.class})
    public List<ConcertSchedule> searchSchedules(Long concertId) {

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
     * @param concertScheduleId 콘서트 날짜 PK
     * @param concertDate 콘서트 날짜
     * @return 콘서트 좌석 정보 List : seatNumber, seatPrice, seatStatus
     */
    @Transactional(readOnly = true, rollbackFor = {Exception.class})
    public List<List<Object>> searchSeats(long concertScheduleId, LocalDate concertDate) {

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

    /**
     * 콘서트 좌석 예약
     * @param userId 유저 PK
     * @param concertScheduleId 콘서트 날짜 PK
     * @param concertDate 콘서트 날짜
     * @param seatNumberList 좌석 번호 리스트
     * @return 예약 정보 리스트 배열
     */
    @Transactional(rollbackFor = {Exception.class})
    public List<Reservation> bookingSeats(long userId, long concertScheduleId, LocalDate concertDate, List<Integer> seatNumberList) {

        //콘서트 날짜 정보 조회
        ConcertSchedule concertSchedule = concertRepository.findByScheduleIdAndConcertDate(concertScheduleId, concertDate);
        //콘서트 정보
        Concert concert = concertSchedule.getConcert();

        //예약하고자 하는 좌석 리스트
        List<ConcertSeat> concertSeats = seatNumberList.stream()
                .map(seatNumber -> concertRepository.findByConcertAndScheduleAndSeatNumber(concert.getId(), concertSchedule.getId(), seatNumber))
                .toList();

        List<Reservation> reservations = new ArrayList<>();
        for (ConcertSeat seat : concertSeats) {
            //예약 가능한 좌석 상태인지 검증
            if (seat.getSeatStatus() != AVAILABLE) {
                throw new CustomBadRequestException(CONCERT_SEAT_IS_NOT_AVAILABLE, "이미 예약되거나 임시 배정 중인 좌석입니다.");
            }
            //임시 배정 상태로 변경 및 유저 PK 추가
            seat.updateSeatStatus(TEMPORARY);
            seat.setUserId(userId);
            concertRepository.saveConcertSeat(seat);

            Reservation reservation = Reservation.create(seat.getId(), userId, concert.getName(), concertDate);
            reservations.add(reservation);
            BigDecimal price = seat.getSeatPrice();
            reservation.setTotalPrice(price);
        }
        return reservations;
    }

    /**
     * 결제 정보 저장
     * @param reservation 예약 객체
     * @param totalPrice 결제 총 금액
     */
    public void savePayment(Reservation reservation, BigDecimal totalPrice) {
        //결제 정보 저장
        Payment payment = Payment.create(reservation, totalPrice);
        concertRepository.savePayment(payment);
    }

    /**
     * 콘서트 좌석 임시 배정 시간 및 예약 만료 시간 체크
     */
    @Transactional(rollbackFor = {Exception.class})
    public void checkExpiredTimeForSeat() {
        LocalDateTime now = LocalDateTime.now();

        //예약 진행 중인 상태의 예약 데이터 조회
        List<Reservation> reservations = concertRepository.findAllByReservationStatus(RESERVING);
        if (reservations.isEmpty()) return;

        reservations.forEach(reservation -> {

            //결제 상태가 완료 상태가 아닌 경우
            if (concertRepository.findPaymentByReservation(reservation).getPaymentState() == COMPLETED) {

                //예약 생성 시간
                LocalDateTime createdAt = reservation.getCreatedAt();
                //예약 가능 시간
                Duration duration = Duration.between(createdAt, now);

                //1분이라고 가정하고, 1분이 지난 경우
                if (duration.toSeconds() > 60) {
                    //예약 취소
                    reservation.updateReservationStatus(CANCELED);
                    concertRepository.saveReservation(reservation);
                    //결제 취소
                    Payment payment = concertRepository.findPaymentByReservation(reservation);
                    payment.updatePaymentStatus(PaymentState.CANCELED);
                    concertRepository.savePayment(payment);
                    //좌석 임시 배정 취소 -> 예약 가능 상태로 변경
                    ConcertSeat concertSeat = concertRepository.findBySeatId(reservation.getConcertSeatId());
                    concertSeat.updateSeatStatus(AVAILABLE);
                    concertRepository.saveConcertSeat(concertSeat);
                    //대기열 토큰 만료
                    WaitingToken waitingToken = waitingTokenRepository.findUsingTokenByUserId(reservation.getUserId());
                    waitingToken.updateWaitingTokenStatus(WaitingTokenStatus.EXPIRED);
                    waitingTokenRepository.save(waitingToken);
                }
            }
        });
    }

    /**
     * 결제
     * @param concertSeatId 콘서트 좌석 PK
     * @param reservationId 예약 PK
     * @return 콘서트 좌석 정보
     */
    @Transactional(rollbackFor = {Exception.class})
    public ConcertSeat pay(Long concertSeatId, Long reservationId) {

        //예약 정보 가져오기
        Reservation reservation = concertRepository.findByReservationId(reservationId);

        //유저 잔액 확인 및 결제 처리
        Long userId = reservation.getUserId();
        User user = waitingTokenRepository.findByUserId(userId);
        Payment payment = concertRepository.findPaymentByReservation(reservation);

        BigDecimal totalAmount = payment.getPrice();
        BigDecimal userAmount = user.getAmount();

        if (userAmount.compareTo(totalAmount) < 0) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }

        //유저 잔액에서 결제 금액 차감
        user.useAmount(totalAmount);
        waitingTokenRepository.saveUser(user);

        //Payment 상태 변경
        payment.updatePaymentStatus(COMPLETED);
        concertRepository.savePayment(payment);

        //ConcertSeat 상태 변경
        ConcertSeat concertSeat = concertRepository.findBySeatId(concertSeatId);
        concertSeat.updateSeatStatus(ConcertSeatStatus.RESERVED);
        concertRepository.saveConcertSeat(concertSeat);

        //Reservation 상태 변경
        reservation.updateReservationStatus(RESERVED);
        concertRepository.saveReservation(reservation);

        return concertSeat;
    }
}
