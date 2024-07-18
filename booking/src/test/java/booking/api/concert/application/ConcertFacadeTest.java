package booking.api.concert.application;

import booking.api.concert.domain.ConcertSchedule;
import booking.api.concert.domain.Reservation;
import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.concert.presentation.response.SearchSeatsResponse;
import booking.common.exception.CustomNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class ConcertFacadeTest {

    @Autowired
    ConcertFacade concertFacade;

    @Test
    @DisplayName("예약 가능한 콘서트 날짜 조회")
    void searchSchedules() {

        long concertId = 1L;

        List<ConcertSchedule> schedules = concertFacade.searchSchedules(concertId);
        List<LocalDate> dates = concertFacade.getConcertScheduleDates(schedules);
        List<Long> idList = concertFacade.getConcertScheduleId(schedules);

        assertThat(dates.size()).isEqualTo(2);
        assertThat(idList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("콘서트 날짜와 일치하는 예약 가능한 좌석 조회")
    void searchSeats() {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");

        List<List<Object>> seatsInfo = concertFacade.searchSeats(concertScheduleId, concertDate);

        List<SearchSeatsResponse> result = new ArrayList<>();
        for (List<Object> details : seatsInfo) {
            int seatNumber = (int) details.get(0);
            BigDecimal price = (BigDecimal) details.get(1);
            ConcertSeatStatus status = ConcertSeatStatus.valueOf((String) details.get(2)); // 문자열을 열거형으로 변환

            result.add(new SearchSeatsResponse(seatNumber, price, status));
        }
        assertThat(result.size()).isEqualTo(50);
    }

    @Test
    @DisplayName("콘서트 좌석 예약 및 결제 정보 저장")
    void bookingSeats() {

        long userId = 1L;
        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");
        List<Integer> concertSeatNumbers = List.of(1, 2, 3);

        List<Reservation> reservations = concertFacade.bookingSeats(userId, concertScheduleId, concertDate, concertSeatNumbers);
        assertThat(reservations.size()).isEqualTo(3);

        for (Reservation reservation : reservations) {
            assertThat(reservation.getConcertName()).isEqualTo("A 콘서트");
        }
    }

    @Test
    @DisplayName("결제하려는 예약이 없는 경우")
    void pay() {

        long concertSeatId = 1L;
        long reservationId = 1L;

        assertThatThrownBy(() -> concertFacade.pay(concertSeatId, reservationId))
                .isInstanceOf(CustomNotFoundException.class)
                .hasMessage("[RESERVATION_IS_NOT_FOUND] 해당하는 예약이 없습니다.");
    }
}