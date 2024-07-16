package booking.api.concert.domain;

import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.concert.domain.enums.ReservationStatus;
import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenRepository;
import booking.common.exception.AuthorizationException;
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
import java.util.stream.Collectors;

import static booking.api.concert.domain.enums.ConcertSeatStatus.AVAILABLE;
import static booking.api.concert.domain.enums.ConcertSeatStatus.TEMPORARY;
import static booking.api.concert.domain.enums.ReservationStatus.RESERVING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
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
    void failToAccessWaiting_schedules() {

        Long concertId = 1L;

        assertThatThrownBy(() -> concertService.searchSchedules(null, concertId))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("토큰 인증에 실패했습니다.");
    }

    @Test
    @DisplayName("콘서트 날짜 정보 조회 성공")
    void searchSchedules() {

        Long concertId = 1L;
        WaitingToken waitingToken = WaitingTokenDummy.getWaitingTokenList().get(0);

        List<LocalDate> dates = concertService.searchSchedules(waitingToken.getToken(), concertId);
        List<Long> idList = concertService.getConcertScheduleId(concertId);

        assertThat(dates.get(0)).isEqualTo("2024-07-10");
        assertThat(dates.get(1)).isEqualTo("2024-07-11");
        assertThat(idList.get(0)).isEqualTo(1L);
        assertThat(idList.get(1)).isEqualTo(2L);
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("콘서트 좌석 조회 - 대기열 검증에 실패한 경우")
    void failToAccessWaiting_seats() {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");

        assertThatThrownBy(() -> concertService.searchSeats(null, concertScheduleId, concertDate))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("토큰 인증에 실패했습니다.");
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
                .collect(Collectors.toList());

        when(concertRepository.findByScheduleIdAndConcertDate(concertScheduleId, concertDate)).thenReturn(concertScheduleList.get(0));
        when(concertRepository.findByConcertAndSchedule(concert, concertSchedule)).thenReturn(concertSeats);

        List<List<Object>> lists = concertService.searchSeats(waitingToken.getToken(), concertScheduleId, concertDate);
        assertThat(lists.size()).isEqualTo(10); //현재 예약 가능한 좌석은 10개라고 가정한다.
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("콘서트 좌석 예약 요청 - 대기열 검증에 실패한 경우")
    void failToAccessWaiting_booking() {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");
        List<Integer> seatNumberList = List.of(1, 2, 8, 9);

        assertThatThrownBy(() -> concertService.bookingSeats(null, 1L, concertScheduleId, concertDate, seatNumberList))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("토큰 인증에 실패했습니다.");
    }

    @Test
    @DisplayName("콘서트 좌석 상태 검증 테스트 - 예약된 좌석 -> 예약 불가")
    void alreadyReservedSeat() {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");
        WaitingToken waitingToken = WaitingTokenDummy.getWaitingTokenList().get(0);
        int seatNumber = 1;
        LocalDateTime now = LocalDateTime.now();
        List<Integer> seatNumberList = List.of(1, 2, 8, 9);

        ConcertSeat expectedConcertSeat = ConcertSeat.create(1L, concert, concertScheduleList.get(0), 1L, seatNumber, BigDecimal.valueOf(1000), TEMPORARY, now, now);
        when(concertRepository.findByScheduleIdAndConcertDate(concertScheduleId, concertDate)).thenReturn(concertScheduleList.get(0));
        when(concertRepository.findByConcertAndScheduleAndSeatNumber(concert.getId(), concertScheduleList.get(0).getId(), seatNumber)).thenReturn(expectedConcertSeat);

        assertThrows(IllegalStateException.class, () -> {
            concertService.bookingSeats(waitingToken.getToken(), 1L, concertScheduleId, concertDate, seatNumberList);
        });
    }

    @Test
    @DisplayName("좌석 상태 검증 테스트 - 예약되지 않은 좌석 -> 예약 가능")
    void validateSeatStatusAvailable() {

        long concertScheduleId = 1L;
        long userId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");
        WaitingToken waitingToken = WaitingTokenDummy.getWaitingTokenList().get(0);
        LocalDateTime now = LocalDateTime.now();
        ConcertSchedule concertSchedule = concertScheduleList.get(0);
        BigDecimal price = BigDecimal.valueOf(1000);

        List<Integer> seatNumberList = List.of(1, 4, 9, 10);
        List<ConcertSeat> seats = List.of(
          ConcertSeat.create(1L, concert, concertSchedule, userId, 1, price, AVAILABLE, now, now),
          ConcertSeat.create(4L, concert, concertSchedule, userId, 4, price, AVAILABLE, now, now),
          ConcertSeat.create(9L, concert, concertSchedule, userId, 9, price, AVAILABLE, now, now),
          ConcertSeat.create(10L, concert, concertSchedule, userId, 10, price, AVAILABLE, now, now)
        );

        when(concertRepository.findByScheduleIdAndConcertDate(concertScheduleId, concertDate)).thenReturn(concertSchedule);

        for (ConcertSeat seat : seats) {
            when(concertRepository.findByConcertAndScheduleAndSeatNumber(concert.getId(), concertSchedule.getId(), seat.getSeatNumber()))
                    .thenReturn(seat);
        }

        for (ConcertSeat concertSeat : concertSeatList) {
            concertSeat.updateSeatStatus(TEMPORARY);
            when(concertRepository.saveConcertSeat(concertSeat)).thenReturn(concertSeat);
        }

        Reservation expectedReservation = new Reservation(1L, concertScheduleId, userId, concert.getName(), concertDate, RESERVING, now, null);
        given(concertRepository.saveReservation(new Reservation(null, concertScheduleId, userId, concert.getName(), concertDate, RESERVING, LocalDateTime.now(), null))).willReturn(expectedReservation);

        Reservation reservation = concertService.bookingSeats(waitingToken.getToken(), userId, concertScheduleId, concertDate, seatNumberList);
        System.out.println("reservation = " + reservation);

        assertThat(reservation).isNotNull();
        assertThat(reservation.getConcertName()).isEqualTo(concert.getName());
        assertThat(reservation.getConcertDate()).isEqualTo(concertDate);
        assertThat(reservation.getReservationStatus()).isEqualTo(RESERVING);
        assertThat(reservation.getCreatedAt()).isNotNull();

        for (ConcertSeat concertSeat : concertSeatList) {
            assertThat(concertSeat.getSeatStatus()).isEqualTo(TEMPORARY);
        }
    }


    @Test
    @DisplayName("예약 만료 시간이 지났을 경우")
    void expiredReservation() {

        String token = "valid_token";
        Long concertSeatId = 1L;
        Long reservationId = 1L;
        Long userId = 1L;

        Reservation reservation = Reservation.create(reservationId, concertSeatId, userId, "A 콘서트", LocalDate.parse("2024-07-10"),
                RESERVING, LocalDateTime.now().minusSeconds(10), null);

        given(concertRepository.findByReservationId(reservationId)).willReturn(reservation);

        assertThrows(RuntimeException.class, () -> {
            concertService.pay(token, concertSeatId, reservationId);
        });
    }

    @Test
    @DisplayName("결제 성공")
    void paySuccess() {

        String token = "valid_token";
        Long concertSeatId = 1L;
        Long reservationId = 1L;
        Long userId = 1L;

        Reservation reservation = Reservation.create(reservationId, concertSeatId, userId, "A 콘서트", LocalDate.parse("2024-07-10"),
                ReservationStatus.RESERVING, LocalDateTime.now(), null);
        when(concertRepository.findByReservationId(reservationId)).thenReturn(reservation);

        ConcertSeat concertSeat = new ConcertSeat(concertSeatId, concert, concertScheduleList.get(0), userId, 1,
                BigDecimal.valueOf(100), ConcertSeatStatus.AVAILABLE, LocalDateTime.now(), null);
        when(concertRepository.findBySeatId(concertSeatId)).thenReturn(concertSeat);

        User user = User.create(userId, BigDecimal.valueOf(500));
        when(waitingTokenRepository.findByUserId(userId)).thenReturn(user);

        ConcertSeat result = concertService.pay(token, concertSeatId, reservationId);

        assertEquals(ConcertSeatStatus.RESERVED, result.getSeatStatus());
    }
}