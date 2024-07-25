package booking.api.concert.domain;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenRepository;
import booking.support.exception.CustomBadRequestException;
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
import static booking.api.concert.domain.enums.PaymentState.COMPLETED;
import static booking.api.concert.domain.enums.ReservationStatus.RESERVING;
import static booking.support.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final WaitingTokenRepository waitingTokenRepository;

    /**
     * 콘서트 목록 조회
     * @return 콘서트 목록
     */
    public List<Concert> getList() {
        return concertRepository.findAllConcerts();
    }

    /**
     * @param concertId 콘서트 PK
     * @return 콘서트 날짜 정보
     */
    public List<ConcertSchedule> getSchedules(Long concertId) {

        //콘서트 정보 조회
        Concert concert = concertRepository.findByConcertId(concertId);

        //콘서트 날짜 정보 조회
        return concertRepository.findSchedulesByConcert(concert);
    }

    /**
     * @param schedules 콘서트 날짜 정보
     * @return 콘서트 날짜 리스트 배열
     */
    public List<LocalDate> getConcertScheduleDates(List<ConcertSchedule> schedules) {
        return schedules.stream()
                .map(ConcertSchedule::getConcertDate)
                .toList();
    }

    /**
     * @param schedules 콘서트 날짜 정보
     * @return 콘서트 날짜 PK 리스트 배열
     */
    public List<Long> getConcertScheduleIds(List<ConcertSchedule> schedules) {
        return schedules.stream()
                .map(ConcertSchedule::getId)
                .toList();
    }

    /**
     * 콘서트 날짜와 일치하는 예약 가능한 좌석 조회
     * @param concertScheduleId 콘서트 날짜 PK
     * @param concertDate 콘서트 날짜
     * @return 콘서트 좌석 정보 List : seatNumber, seatPrice, seatStatus
     */
    public List<List<Object>> getSeats(long concertScheduleId, LocalDate concertDate) {

        //날짜와 일치하는 콘서트 날짜 정보 조회
        ConcertSchedule concertSchedule = concertRepository.findScheduleByDate(concertScheduleId, concertDate);

        //예약 가능한 콘서트 좌석 조회
        List<ConcertSeat> concertSeats = concertRepository.findSeats(concertSchedule.getConcert(), concertSchedule)
                .stream()
                .filter(seat -> seat.getSeatStatus() == AVAILABLE)
                .toList();

        //예약 가능한 콘서트 좌석이 없는 경우 예약할 수 없다.
        if (concertSeats.isEmpty()) throw new CustomBadRequestException(CONCERT_SEAT_ALL_RESERVED, "매진되었습니다.");

        return concertSeats.stream()
                .map(seat -> {
                    List<Object> seatInfo = new ArrayList<>();
                    seatInfo.add(seat.getSeatNumber());
                    seatInfo.add(seat.getSeatPrice());
                    seatInfo.add(seat.getSeatStatus().name());
                    return seatInfo;
                })
                .toList();
    }

    /**
     * 콘서트 좌석 예약
     * @param userId 유저 PK
     * @param concertScheduleId 콘서트 날짜 PK
     * @param concertDate 콘서트 날짜
     * @param seatNumberList 좌석 번호 리스트
     * @return 예약 정보 리스트 배열
     */
    @Transactional
    public List<Reservation> bookingSeats(long userId, long concertScheduleId, LocalDate concertDate, List<Integer> seatNumberList) {

        //콘서트 날짜 정보 조회
        ConcertSchedule concertSchedule = concertRepository.findScheduleByDate(concertScheduleId, concertDate);
        //콘서트 정보
        Concert concert = concertSchedule.getConcert();

        //예약 정보 저장
        List<Reservation> reservations = getReservations(userId, concertDate, seatNumberList, concert, concertSchedule);

        //결제 정보 저장
        reservations.forEach(reservation ->
                concertRepository.savePayment(Payment.create(reservation.getId(), reservation.getTotalAmount())));
        return reservations;
    }

    private List<Reservation> getReservations(long userId, LocalDate concertDate, List<Integer> seatNumberList, Concert concert, ConcertSchedule concertSchedule) {

        //예약하고자 하는 좌석 리스트
        List<ConcertSeat> concertSeats = getConcertSeats(userId, seatNumberList, concert, concertSchedule);

        return concertSeats.stream()
                .map(seat -> {
                    BigDecimal price = seat.getSeatPrice();

                    //예약 정보 생성
                    Reservation reservation = Reservation.create(seat.getId(), userId, concert.getName(), concertDate);
                    reservation.setTotalPrice(price);
                    return concertRepository.saveReservation(reservation);
                })
                .toList();
    }

    private List<ConcertSeat> getConcertSeats(long userId, List<Integer> seatNumberList, Concert concert, ConcertSchedule concertSchedule) {

        List<ConcertSeat> concertSeats = seatNumberList.stream()
                .map(seatNumber -> concertRepository.findSeatsBySeatNumber(concert.getId(), concertSchedule.getId(), seatNumber))
                .peek(seat -> {
                    //예약 가능한 좌석 상태인지 검증
                    if (seat.getSeatStatus() != AVAILABLE) {
                        throw new CustomBadRequestException(CONCERT_SEAT_IS_NOT_AVAILABLE, "이미 예약되거나 임시 배정 중인 좌석입니다.");
                    }
                    //좌석 상태 변경, 유저 PK 및 변경 시간 업데이트
                    seat.temporarySeat();
                    seat.setUserId(userId);
                    seat.updateTime();
                })
                .toList();

        //좌석 정보 저장
        return concertRepository.saveAllSeats(concertSeats);
    }

    /**
     * 콘서트 좌석 임시 배정 시간 및 예약 만료 시간 체크
     */
    @Transactional
    public void expiredToken() {

        LocalDateTime now = LocalDateTime.now();

        //예약 진행 중인 상태의 예약 데이터 조회
        List<Reservation> reservations = concertRepository.findAllByReservationStatus(RESERVING);
        if (reservations.isEmpty()) return;

        reservations.forEach(reservation -> {
            Payment paymentByReservation = concertRepository.findPaymentByReservation(reservation.getId());

            //결제 상태가 완료 상태가 아닌 경우
            if (paymentByReservation.getPaymentState() != COMPLETED) {

                //예약 생성 시간
                LocalDateTime createdAt = reservation.getCreatedAt();

                //예약 가능 시간
                Duration duration = Duration.between(createdAt, now);

                //1분이라고 가정하고, 1분이 지난 경우
                if (duration.toSeconds() > 60) {
                    //예약 취소
                    reservation.canceledReservation();
                    concertRepository.saveReservation(reservation);

                    //결제 취소
                    Payment payment = concertRepository.findPaymentByReservation(reservation.getId());
                    payment.canceledPayment();
                    concertRepository.savePayment(payment);

                    //좌석 임시 배정 취소 -> 예약 가능 상태로 변경
                    ConcertSeat concertSeat = concertRepository.findBySeatId(reservation.getConcertSeatId());
                    concertSeat.availableSeat();
                    concertRepository.saveConcertSeat(concertSeat);

                    //대기열 토큰 만료
                    WaitingToken waitingToken = waitingTokenRepository.findNotExpiredToken(reservation.getUserId());
                    waitingToken.expiredToken();
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
    @Transactional
    public ConcertSeat pay(Long concertSeatId, Long reservationId) {

        //예약 정보 가져오기
        Reservation reservation = concertRepository.findByReservationId(reservationId);

        //유저 잔액 확인 및 결제 처리
        Long userId = reservation.getUserId();
        User user = waitingTokenRepository.findLockByUserId(userId);
        Payment payment = concertRepository.findPaymentByReservation(reservation.getId());

        BigDecimal totalAmount = payment.getPrice();
        BigDecimal userAmount = user.getAmount();

        if (userAmount.compareTo(totalAmount) < 0) {
            throw new CustomBadRequestException(USER_AMOUNT_IS_NOT_ENOUGH, "잔액이 부족합니다.");
        }

        //유저 잔액에서 결제 금액 차감
        user.useAmount(totalAmount);
        waitingTokenRepository.saveUser(user);

        //Payment 상태 변경
        payment.completedPayment();
        concertRepository.savePayment(payment);

        //ConcertSeat 상태 변경
        ConcertSeat concertSeat = concertRepository.findBySeatId(concertSeatId);
        concertSeat.reservedSeat();
        concertRepository.saveConcertSeat(concertSeat);

        //Reservation 상태 변경
        reservation.reservedReservation();
        concertRepository.saveReservation(reservation);

        return concertSeat;
    }
}
