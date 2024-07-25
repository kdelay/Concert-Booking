package booking.api.concert.domain;

import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.concert.domain.enums.PaymentState;
import booking.api.concert.domain.enums.ReservationStatus;
import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingTokenRepository;
import booking.dummy.ConcertSeatDummy;
import booking.support.exception.CustomBadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static booking.api.concert.domain.enums.ConcertSeatStatus.AVAILABLE;
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
        lenient().when(concertRepository.findSchedulesByConcert(concert)).thenReturn(concertScheduleList);

        concertSeatList = ConcertSeatDummy.getConcertSeatList(concert, concertScheduleList);
        for (ConcertSeat seat : concertSeatList) {
            lenient().when(concertRepository.saveConcertSeat(seat)).thenReturn(seat);
            lenient().when(concertRepository.findBySeatId(seat.getId())).thenReturn(seat);
        }
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("콘서트 조회")
    void getList() {
        List<Concert> concerts = List.of(
            new Concert(1L, "A 콘서트", "A"),
            new Concert(1L, "A 콘서트", "B")
        );
        when(concertRepository.findAllConcerts()).thenReturn(concerts);

        List<Concert> concertList = concertService.getList();
        assertThat(concertList).hasSize(2);
    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜 및 PK 조회")
    void getSchedules() {

        //콘서트 날짜 정보 조회
        when(concertRepository.findSchedulesByConcert(concert)).thenReturn(concertScheduleList);

        List<ConcertSchedule> concertSchedules = concertService.getSchedules(concert.getId());
        List<LocalDate> dates = concertService.getConcertScheduleDates(concertSchedules);
        List<Long> idList = concertService.getConcertScheduleIds(concertSchedules);

        //콘서트 날짜 및 Pk 검증
        assertThat(dates.get(0)).isEqualTo("2024-07-10");
        assertThat(dates.get(1)).isEqualTo("2024-07-11");
        assertThat(idList.get(0)).isEqualTo(1L);
        assertThat(idList.get(1)).isEqualTo(2L);
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("이미 모든 좌석이 예약된 경우")
    void alreadyAllSeatReserved() {

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
        when(concertRepository.findScheduleByDate(concertScheduleId, concertDate)).thenReturn(concertSchedule);

        //예약 가능한 콘서트 좌석 조회
        when(concertRepository.findSeats(concert, concertSchedule)).thenReturn(concertSeats);

        //콘서트 날짜와 일치하는 예약 가능한 좌석 조회 검증
        assertThatThrownBy(() -> concertService.getSeats(concertScheduleId, concertDate))
                .isInstanceOf(CustomBadRequestException.class)
                .hasMessage("[CONCERT_SEAT_ALL_RESERVED] 매진되었습니다.");
    }

    @Test
    @DisplayName("콘서트 좌석 정보 조회 성공")
    void getSeats() {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");
        ConcertSchedule concertSchedule = concertScheduleList.get(0);

        List<ConcertSeat> concertSeats = concertSeatList.stream()
                .filter(seat -> seat.getConcert().equals(concert))
                .filter(seat -> seat.getConcertSchedule().equals(concertSchedule))
                .filter(seat -> seat.getSeatStatus() == AVAILABLE)
                .toList();

        //날짜와 일치하는 콘서트 날짜 정보 조회
        when(concertRepository.findScheduleByDate(concertScheduleId, concertDate)).thenReturn(concertSchedule);

        //예약 가능한 콘서트 좌석 조회
        when(concertRepository.findSeats(concert, concertSchedule)).thenReturn(concertSeats);

        List<List<Object>> lists = concertService.getSeats(concertScheduleId, concertDate);
        assertThat(lists.size()).isEqualTo(20); //현재 예약 가능한 좌석은 20개라고 가정한다.
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("콘서트 좌석 상태 검증 테스트 - 예약된 좌석 -> 예약 불가")
    void alreadyReservedSeat() {

        ConcertSchedule concertSchedule = concertScheduleList.get(0);

        List<Integer> seatNumberList = List.of(1, 3);
        List<ConcertSeat> seats = ConcertSeatDummy.getAllSeatReserved(concert, concertScheduleList);

        List<ConcertSeat> concertSeats = seats.stream()
                .filter(seat -> seatNumberList.contains(seat.getSeatNumber()))
                .toList();

        when(concertRepository.findScheduleByDate(anyLong(), any(LocalDate.class))).thenReturn(concertSchedule);

        concertSeats.forEach(concertSeat -> {
            when(concertRepository.findSeatsBySeatNumber(anyLong(), anyLong(), anyInt()))
                    .thenReturn(concertSeat);
        });

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
        when(concertRepository.findScheduleByDate(anyLong(), any(LocalDate.class))).thenReturn(concertSchedule);

        //예약하고자 하는 좌석 리스트
        concertSeats.forEach(concertSeat -> {
            when(concertRepository.findSeatsBySeatNumber(anyLong(), anyLong(), eq(concertSeat.getSeatNumber())))
                    .thenReturn(concertSeat);
        });

        //예약 정보
        concertSeats.forEach(concertSeat -> {
            Reservation reservation = Reservation.create(concertSeat.getId(), 1L, concert.getName(), concertSchedule.getConcertDate());
            reservation.setTotalPrice(concertSeat.getSeatPrice());
            when(concertRepository.saveReservation(any(Reservation.class))).thenReturn(reservation);
        });

        List<Reservation> reservations = concertService.bookingSeats(1L, 1L, LocalDate.now(), seatNumberList);

        assertEquals(2, reservations.size());
        assertThat(reservations).extracting(Reservation::getConcertSeatId).containsExactlyInAnyOrder(2L, 4L);
    }

    @Test
    @Transactional
    @DisplayName("예약 만료 시간이 지났을 경우")
    void expiredToken() {

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

        reservations.forEach(reservation -> {
            //예약 취소
            reservation.canceledReservation();

            //결제 취소
            Payment payment = Payment.create(reservation.getId(), BigDecimal.valueOf(1000));
            lenient().when(concertRepository.findPaymentByReservation(reservation.getId())).thenReturn(payment);
            payment.canceledPayment();
        });

        //좌석 임시 배정 취소 -> 예약 가능 상태로 변경
        concertSeat.availableSeat();
        lenient().when(concertRepository.findBySeatId(concertSeat.getId())).thenReturn(concertSeat);

        //콘서트 좌석 임시 배정 시간 및 예약 만료 시간 체크
        concertService.expiredToken();

        reservations.forEach(reservation ->  {
            Payment payment = concertRepository.findPaymentByReservation(reservation.getId());
            assertEquals(CANCELED, reservation.getReservationStatus());
            assertEquals(PaymentState.CANCELED, payment.getPaymentState());
            assertEquals(AVAILABLE, concertSeat.getSeatStatus());
        });
    }

    @Test
    @Transactional
    @DisplayName("결제 성공")
    void paySuccess() {

        Long concertSeatId = 1L;
        Long reservationId = 1L;
        Long userId = 1L;
        BigDecimal seatPrice = concertSeatList.get(0).getSeatPrice();

        //예약 정보 조회
        Reservation reservation = Reservation.create(concertSeatId, userId, "A 콘서트", LocalDate.parse("2024-07-10"));
        when(concertRepository.findByReservationId(reservationId)).thenReturn(reservation);

        //유저 잔액 조회
        User user = User.create(userId, BigDecimal.valueOf(5000));
        when(waitingTokenRepository.findLockByUserId(anyLong())).thenReturn(user);

        //결제 정보 조회
        Payment payment = new Payment(1L, reservation.getId(), seatPrice, PaymentState.PENDING, LocalDateTime.now(), null);
        when(concertRepository.findPaymentByReservation(reservation.getId())).thenReturn(payment);

        //좌석 상태 변경
        ConcertSeat concertSeat = new ConcertSeat(concertSeatId, 0L, concert, concertScheduleList.get(0), userId, 1,
                BigDecimal.valueOf(100), ConcertSeatStatus.AVAILABLE, LocalDateTime.now(), null);
        when(concertRepository.findBySeatId(anyLong())).thenReturn(concertSeat);

        ConcertSeat result = concertService.pay(concertSeatId, reservationId);

        //유저 잔액 차감 검증
        BigDecimal expectedAmount = waitingTokenRepository.findLockByUserId(user.getId()).getAmount();
        assertThat(expectedAmount).isEqualTo(BigDecimal.valueOf(5000).subtract(seatPrice));

        //결제 상태 COMPLETED 검증
        assertEquals(payment.getPaymentState(), PaymentState.COMPLETED);

        //좌석 상태 RESERVED 검증
        assertEquals(result.getSeatStatus(), ConcertSeatStatus.RESERVED);

        //예약 상태 RESERVED 검증
        assertEquals(reservation.getReservationStatus(), ReservationStatus.RESERVED);
    }

    @Test
    @DisplayName("유저 잔액이 부족한 경우")
    void notEnoughUserAmount() {
        Long concertSeatId = 1L;
        Long reservationId = 1L;
        Long userId = 1L;
        BigDecimal seatPrice = concertSeatList.get(0).getSeatPrice();

        //예약 정보 조회
        Reservation reservation = Reservation.create(concertSeatId, userId, "A 콘서트", LocalDate.parse("2024-07-10"));
        when(concertRepository.findByReservationId(reservationId)).thenReturn(reservation);

        //유저 잔액 조회
        User user = User.create(userId, BigDecimal.valueOf(500));
        when(waitingTokenRepository.findLockByUserId(anyLong())).thenReturn(user);

        //결제 정보 조회
        Payment payment = new Payment(1L, reservation.getId(), seatPrice, PaymentState.PENDING, LocalDateTime.now(), null);
        when(concertRepository.findPaymentByReservation(reservation.getId())).thenReturn(payment);

        assertThatThrownBy(() -> concertService.pay(concertSeatId, reservationId))
                .isInstanceOf(CustomBadRequestException.class)
                .hasMessage("[USER_AMOUNT_IS_NOT_ENOUGH] 잔액이 부족합니다.");
    }
}