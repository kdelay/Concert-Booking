package booking.api.concert.domain;

import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.concert.domain.enums.PaymentState;
import booking.api.concert.domain.enums.ReservationStatus;
import booking.api.waiting.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static booking.api.concert.domain.enums.ConcertSeatStatus.RESERVED;
import static booking.api.concert.domain.enums.ConcertSeatStatus.TEMPORARY;
import static booking.api.concert.domain.enums.ReservationStatus.*;
import static booking.api.concert.domain.enums.ReservationStatus.CANCELED;
import static org.assertj.core.api.Assertions.*;

public class ConcertDomainTest {

    private User user;
    private Concert concert;
    private List<ConcertSchedule> concertScheduleList;
    private ConcertSeat concertSeat;
    private Reservation reservation;
    private Payment payment;

    @BeforeEach
    void setUp() {
        user = new User(1L, BigDecimal.valueOf(2000));
        concert = new Concert(1L, "A 콘서트", "A");
        concertScheduleList = new ArrayList<>(List.of(
           new ConcertSchedule(1L, concert, LocalDate.parse("2024-07-18")),
           new ConcertSchedule(2L, concert, LocalDate.parse("2024-07-19"))
        ));
        concertSeat = new ConcertSeat(1L, concert, concertScheduleList.get(0), null, 1, BigDecimal.valueOf(1000),
                ConcertSeatStatus.AVAILABLE, null, null);
        reservation = new Reservation(1L, 1L, 1L, concert.getName(), concertScheduleList.get(0).getConcertDate(),
                RESERVING, LocalDateTime.now(), null);
        payment = new Payment(1L, reservation, BigDecimal.valueOf(1000),
                PaymentState.PENDING, LocalDateTime.now(), null);
    }

    @Test
    @DisplayName("콘서트 좌석 상태 변경")
    void updateSeatStatus() {

        concertSeat.updateSeatStatus(TEMPORARY);
        assertThat(concertSeat.getSeatStatus()).isEqualTo(TEMPORARY);

        concertSeat.updateSeatStatus(RESERVED);
        assertThat(concertSeat.getSeatStatus()).isEqualTo(RESERVED);
    }

    @Test
    @DisplayName("유저 PK 설정")
    void setUserId() {

        long userId = 1L;

        concertSeat.setUserId(userId);
        assertThat(concertSeat.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("예약 총 금액")
    void setTotalPrice() {
        reservation.setTotalPrice(BigDecimal.valueOf(2000));
        assertThat(reservation.getTotalAmount().compareTo(BigDecimal.valueOf(2000))).isEqualTo(0);
    }

    @Test
    @DisplayName("예약 상태 변경")
    void updateReservationStatus() {
        reservation.updateReservationStatus(ReservationStatus.RESERVED);
        assertThat(reservation.getReservationStatus()).isEqualTo(ReservationStatus.RESERVED);

        reservation.updateReservationStatus(CANCELED);
        assertThat(reservation.getReservationStatus()).isEqualTo(CANCELED);
    }

    @Test
    @DisplayName("결제 상태 변경")
    void updatePaymentStatus() {
        payment.updatePaymentStatus(PaymentState.COMPLETED);
        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.COMPLETED);

        payment.updatePaymentStatus(PaymentState.CANCELED);
        assertThat(payment.getPaymentState()).isEqualTo(PaymentState.CANCELED);
    }
}
