package booking.api.concert.interfaces;

import booking.api.concert.domain.*;
import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.concert.interfaces.request.BookingSeatsRequest;
import booking.api.concert.interfaces.request.PayRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static booking.api.concert.domain.enums.ReservationStatus.RESERVING;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConcertController.class)
class ConcertControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ConcertService concertService;

    @Test
    @DisplayName("GET /concert/list 콘서트 목록 조회")
    void getList() throws Exception {

        List<Concert> concerts = new ArrayList<>(List.of(
                new Concert(1L, "A 콘서트", "A"),
                new Concert(2L, "B 콘서트", "B")
        ));
        given(concertService.getConcertsWithCache()).willReturn(concerts);

        mockMvc.perform(get("/concert/list"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /concert/schedules/{concertId} 콘서트 예약 가능 날짜 조회")
    void getSchedules() throws Exception {

        Long concertId = 1L;
        String token = UUID.randomUUID().toString();

        Concert concert = Concert.create(concertId, "A 콘서트", "A");
        List<ConcertSchedule> concertSchedules = List.of(
                ConcertSchedule.create(1L, concert, LocalDate.parse("2024-07-10")),
                ConcertSchedule.create(2L, concert, LocalDate.parse("2024-07-11"))
        );

        List<LocalDate> dates = new ArrayList<>();
        List<Long> idList = new ArrayList<>();
        for (ConcertSchedule schedule : concertSchedules) {
            dates.add(schedule.getConcertDate());
            idList.add(schedule.getId());
        }

        given(concertService.getSchedulesWithCache(concertId)).willReturn(concertSchedules);
        given(concertService.getConcertScheduleDates(concertSchedules)).willReturn(dates);
        given(concertService.getConcertScheduleIds(concertSchedules)).willReturn(idList);

        mockMvc.perform(get("/concert/schedules/{concertId}", concertId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /concert/seats/{concertScheduleId} 콘서트 예약 가능 좌석 조회")
    void getSeats() throws Exception {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");
        String token = UUID.randomUUID().toString();

        List<List<Object>> seatsInfo = List.of(
                List.of(1, BigDecimal.valueOf(1000), "AVAILABLE"),
                List.of(2, BigDecimal.valueOf(1000), "AVAILABLE")
        );
        given(concertService.getSeats(concertScheduleId, concertDate)).willReturn(seatsInfo);

        mockMvc.perform(get("/concert/seats/{concertScheduleId}", concertScheduleId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /concert/seats/booking 콘서트 좌석 예약 및 결제 정보 저장")
    void bookingSeats() throws Exception {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");
        List<Integer> concertSeatNumbers = List.of(1, 2, 3);
        long userId = 1L;
        String concertName = "A 콘서트";
        String token = "valid-token";

        BookingSeatsRequest bookingSeatsRequest = new BookingSeatsRequest(concertScheduleId, concertDate, concertSeatNumbers, userId);

        String req = objectMapper.writeValueAsString(bookingSeatsRequest);

        List<Reservation> reservations = List.of(
            new Reservation(1L, 1L, userId, concertName, concertDate, RESERVING, LocalDateTime.now(), null),
            new Reservation(2L, 2L, userId, concertName, concertDate, RESERVING, LocalDateTime.now(), null),
            new Reservation(3L, 3L, userId, concertName, concertDate, RESERVING, LocalDateTime.now(), null)
        );
        given(concertService.bookingSeats(userId, concertScheduleId, concertDate, concertSeatNumbers, "")).willReturn(reservations);

        mockMvc.perform(post("/concert/seats/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /concert/payment 결제")
    void pay() throws Exception {

        Concert concert = Concert.create(1L, "A 콘서트", "A");
        ConcertSchedule concertSchedule = ConcertSchedule.create(1L, concert, LocalDate.parse("2024-07-10"));
        ConcertSeat concertSeat = new ConcertSeat(1L, 0L, concert, concertSchedule, 1L, 1, BigDecimal.valueOf(1000), ConcertSeatStatus.TEMPORARY, null, null);
        given(concertService.pay(1L, 1L, "")).willReturn(concertSeat);

        PayRequest payRequest = new PayRequest(1L, 1L);
        String req = objectMapper.writeValueAsString(payRequest);

        mockMvc.perform(post("/concert/payment")
                        .header("Authorization", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}