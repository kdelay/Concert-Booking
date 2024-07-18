package booking.api.concert.domain;

import booking.api.concert.Payment;
import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.concert.domain.enums.PaymentState;
import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenRepository;
import booking.common.exception.CustomBadRequestException;
import booking.common.exception.CustomNotFoundException;
import booking.dummy.ConcertSeatDummy;
import booking.dummy.WaitingTokenDummy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static booking.api.concert.domain.enums.ConcertSeatStatus.AVAILABLE;
import static booking.api.concert.domain.enums.ConcertSeatStatus.TEMPORARY;
import static booking.api.concert.domain.enums.ReservationStatus.CANCELED;
import static booking.api.concert.domain.enums.ReservationStatus.RESERVING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @InjectMocks
    ConcertService concertService;

    @Mock
    ConcertRepository concertRepository;

    @Mock
    WaitingTokenRepository waitingTokenRepository;

    private Concert concert;
    private List<ConcertSchedule> concertScheduleList;
    private List<ConcertSeat> concertSeatList;

    @BeforeEach
    void setUp() {

        //콘서트 데이터 기본 세팅
        concert = Concert.create(1L, "A 콘서트", "A");
        lenient().when(concertRepository.findByConcertId(concert.getId())).thenReturn(concert);

        concertScheduleList = List.of(
            ConcertSchedule.create(1L, concert, LocalDate.parse("2024-07-10")),
            ConcertSchedule.create(2L, concert, LocalDate.parse("2024-07-11"))
        );
        lenient().when(concertRepository.findByConcertEntity(concert)).thenReturn(concertScheduleList);

        concertSeatList = ConcertSeatDummy.getConcertSeatList(concert, concertScheduleList);
        for (ConcertSeat seat : concertSeatList) {
            lenient().when(concertRepository.saveConcertSeat(seat)).thenReturn(seat);
            lenient().when(concertRepository.findBySeatId(seat.getId())).thenReturn(seat);
        }
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("콘서트 날짜 조회 - 대기열 검증에 실패한 경우")
    void waitingTokenAuthFailForSchedule() {

        long concertId = 1L;

        assertThatThrownBy(() -> concertService.searchSchedules(concertId))
                .isInstanceOf(CustomNotFoundException.class)
                .hasMessage("[WAITING_TOKEN_AUTH_FAIL] 토큰 인증에 실패했습니다.");
    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜 및 PK 조회 성공")
    void searchSchedules() {

        //콘서트 날짜 정보 조회
        when(concertRepository.findByConcertEntity(concert)).thenReturn(concertScheduleList);

        List<ConcertSchedule> concertSchedules = concertService.searchSchedules(concert.getId());
        List<LocalDate> dates = concertService.getConcertScheduleDates(concertSchedules);
        List<Long> idList = concertService.getConcertScheduleId(concertSchedules);

        //콘서트 날짜 및 Pk 검증
        assertThat(dates.get(0)).isEqualTo("2024-07-10");
        assertThat(dates.get(1)).isEqualTo("2024-07-11");
        assertThat(idList.get(0)).isEqualTo(1L);
        assertThat(idList.get(1)).isEqualTo(2L);
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("콘서트 좌석 조회 - 대기열 검증에 실패한 경우")
    void waitingTokenAuthFailForSeat() {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");

        assertThatThrownBy(() -> concertService.searchSeats(concertScheduleId, concertDate))
                .isInstanceOf(CustomNotFoundException.class)
                .hasMessage("[WAITING_TOKEN_AUTH_FAIL] 토큰 인증에 실패했습니다.");
    }

    @Test
    @DisplayName("이미 모든 좌석이 예약된 경우")
    void alreadyAllSeatReserved() {

        String token = "valid-token";
        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-17");


        List<ConcertSeat> seats = ConcertSeatDummy.getAllSeatReserved(concert, concertScheduleList);
        ConcertSchedule concertSchedule = concertScheduleList.get(0);

        List<ConcertSeat> concertSeats = seats.stream()
                .filter(seat -> seat.getConcert().equals(concert))
                .filter(seat -> seat.getConcertSchedule().equals(concertSchedule))
                .filter(seat -> seat.getSeatStatus() == AVAILABLE)
                .toList();

        //날짜와 일치하는 콘서트 날짜 정보 조회
        when(concertRepository.findByScheduleIdAndConcertDate(concertScheduleId, concertDate)).thenReturn(concertSchedule);

        //예약 가능한 콘서트 좌석 조회
        when(concertRepository.findByConcertAndSchedule(concert, concertSchedule)).thenReturn(concertSeats);

        //콘서트 날짜와 일치하는 예약 가능한 좌석 조회 검증
        assertThatThrownBy(() -> concertService.searchSeats(concertScheduleId, concertDate))
                .isInstanceOf(CustomBadRequestException.class)
                .hasMessage("[CONCERT_SEAT_ALL_RESERVED] 매진되었습니다.");
    }

    @Test
    @DisplayName("콘서트 좌석 정보 조회 성공")
    void searchSeats() {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");
        WaitingToken waitingToken = WaitingTokenDummy.getWaitingTokenList().get(0);
        ConcertSchedule concertSchedule = concertScheduleList.get(0);

        List<ConcertSeat> concertSeats = concertSeatList.stream()
                .filter(seat -> seat.getConcert().equals(concert))
                .filter(seat -> seat.getConcertSchedule().equals(concertSchedule))
                .filter(seat -> seat.getSeatStatus() == AVAILABLE)
                .toList();

        //날짜와 일치하는 콘서트 날짜 정보 조회
        when(concertRepository.findByScheduleIdAndConcertDate(concertScheduleId, concertDate)).thenReturn(concertSchedule);

        //예약 가능한 콘서트 좌석 조회
        when(concertRepository.findByConcertAndSchedule(concert, concertSchedule)).thenReturn(concertSeats);

        List<List<Object>> lists = concertService.searchSeats(concertScheduleId, concertDate);
        assertThat(lists.size()).isEqualTo(20); //현재 예약 가능한 좌석은 20개라고 가정한다.
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("콘서트 좌석 예약 요청 - 대기열 검증에 실패한 경우")
    void failToAccessWaiting_booking() {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");
        List<Integer> seatNumberList = List.of(1, 2, 8, 9);

        assertThatThrownBy(() -> concertService.bookingSeats(1L, concertScheduleId, concertDate, seatNumberList))
                .isInstanceOf(CustomNotFoundException.class)
                .hasMessage("[WAITING_TOKEN_AUTH_FAIL] 토큰 인증에 실패했습니다.");
    }

    @Test
    @DisplayName("콘서트 좌석 상태 검증 테스트 - 예약된 좌석 -> 예약 불가")
    void alreadyReservedSeat() {

        ConcertSchedule concertSchedule = concertScheduleList.get(0);

        List<Integer> seatNumberList = List.of(1, 3);
        List<ConcertSeat> seats = ConcertSeatDummy.getAllSeatReserved(concert, concertScheduleList);
        List<ConcertSeat> concertSeats = seats.stream()
                .filter(seat -> seatNumberList.contains(seat.getSeatNumber()))
                .filter(seat -> seat.getConcert().equals(concert))
                .filter(seat -> seat.getConcertSchedule().equals(concertSchedule))
                .toList();

        when(concertRepository.findByScheduleIdAndConcertDate(anyLong(), any(LocalDate.class))).thenReturn(concertSchedule);

        for (ConcertSeat concertSeat : concertSeats) {
            when(concertRepository.findByConcertAndScheduleAndSeatNumber(anyLong(), anyLong(), eq(concertSeat.getSeatNumber()))).thenReturn(concertSeat);
        }

        assertThatThrownBy(() -> concertService.bookingSeats(1L, 1L, LocalDate.now(), seatNumberList))
                .isInstanceOf(CustomBadRequestException.class)
                .hasMessage("[CONCERT_SEAT_IS_NOT_AVAILABLE] 이미 예약되거나 임시 배정 중인 좌석입니다.");
    }

    @Test
    @DisplayName("콘서트 좌석 상태 검증 테스트 - 예약되지 않은 좌석 -> 예약 가능")
    void validateSeatStatusAvailable() {

        List<Integer> seatNumberList = List.of(2, 4);
        ConcertSchedule concertSchedule = concertScheduleList.get(0);
        List<ConcertSeat> seats = ConcertSeatDummy.getConcertSeatList(concert, concertScheduleList);
        List<ConcertSeat> concertSeats = seats.stream()
                .filter(seat -> seatNumberList.contains(seat.getSeatNumber()))
                .filter(seat -> seat.getConcert().equals(concert))
                .filter(seat -> seat.getConcertSchedule().equals(concertSchedule))
                .toList();

        //콘서트 날짜 정보 조회
        when(concertRepository.findByScheduleIdAndConcertDate(anyLong(), any(LocalDate.class))).thenReturn(concertSchedule);

        //예약하고자 하는 좌석 리스트
        for (ConcertSeat seat : concertSeats) {
            when(concertRepository.findByConcertAndScheduleAndSeatNumber(anyLong(), anyLong(), eq(seat.getSeatNumber()))).thenReturn(seat);
        }

        List<Reservation> reservations = concertService.bookingSeats(1L, 1L, LocalDate.now(), seatNumberList);

        //임시 배정 상태로 변경
        for (ConcertSeat seat : concertSeats) {
            seat.updateSeatStatus(TEMPORARY);
        }

        assertEquals(2, reservations.size());
        assertThat(reservations.get(0).getConcertSeatId()).isEqualTo(2);
        assertThat(reservations.get(1).getConcertSeatId()).isEqualTo(4);
    }

    @Test
    @DisplayName("예약 만료 시간이 지났을 경우")
    void checkExpiredTimeForSeat() {

        //현재 시간
        LocalDateTime now = LocalDateTime.now();
        //10초 전 생성된 예약
        LocalDateTime createdAt = now.minusSeconds(10);

        ConcertSeat concertSeat = concertSeatList.get(0);
        ConcertSchedule concertSchedule = concertScheduleList.get(0);

        List<Reservation> reservations = List.of(
            new Reservation(1L, concertSeat.getId(), 1L, concert.getName(), concertSchedule.getConcertDate(),
                  RESERVING, createdAt, null),
            new Reservation(1L, concertSeat.getId(), 1L, concert.getName(), concertSchedule.getConcertDate(),
                RESERVING, createdAt, null)
        );
        when(concertRepository.findAllByReservationStatus(RESERVING)).thenReturn(reservations);

        Payment payment = null;
        for (Reservation reservation : reservations) {
            //예약 취소
            reservation.updateReservationStatus(CANCELED);

            //결제 취소
            payment = Payment.create(reservation, BigDecimal.valueOf(1000));
            lenient().when(concertRepository.findPaymentByReservation(reservation)).thenReturn(payment);
            payment.updatePaymentStatus(PaymentState.CANCELED);

        }
        //좌석 임시 배정 취소 -> 예약 가능 상태로 변경
        concertSeat.updateSeatStatus(AVAILABLE);
        lenient().when(concertRepository.findBySeatId(concertSeat.getId())).thenReturn(concertSeat);

        //콘서트 좌석 임시 배정 시간 및 예약 만료 시간 체크
        concertService.checkExpiredTimeForSeat();

        for (Reservation reservation : reservations) {
            assertEquals(CANCELED, reservation.getReservationStatus());
            assertEquals(PaymentState.CANCELED, payment.getPaymentState());
            assertEquals(AVAILABLE, concertSeat.getSeatStatus());
        }
    }

    @Test
    @DisplayName("결제 성공")
    void paySuccess() {

        Long concertSeatId = 1L;
        Long reservationId = 1L;
        Long userId = 1L;

        Reservation reservation = Reservation.create(concertSeatId, userId, "A 콘서트", LocalDate.parse("2024-07-10"));
        when(concertRepository.findByReservationId(reservationId)).thenReturn(reservation);

        ConcertSeat concertSeat = new ConcertSeat(concertSeatId, concert, concertScheduleList.get(0), userId, 1,
                BigDecimal.valueOf(100), ConcertSeatStatus.AVAILABLE, LocalDateTime.now(), null);
        when(concertRepository.findBySeatId(concertSeatId)).thenReturn(concertSeat);

        User user = User.create(userId, BigDecimal.valueOf(500));
        when(waitingTokenRepository.findByUserId(userId)).thenReturn(user);

        ConcertSeat result = concertService.pay(concertSeatId, reservationId);

        assertEquals(ConcertSeatStatus.RESERVED, result.getSeatStatus());
    }
}