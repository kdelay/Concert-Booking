package booking.concert.presentation.controller;

import booking.concert.presentation.request.BookingSeatsRequest;
import booking.concert.presentation.request.WaitingTokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Random;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConcertController.class)
class ConcertControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /concert/waiting/token 유저 대기열 토큰 발급")
    void waitingToken() throws Exception {

        WaitingTokenRequest waitingTokenRequest = new WaitingTokenRequest(anyLong(), anyLong());
        String req = objectMapper.writeValueAsString(waitingTokenRequest);

        mockMvc.perform(post("/concert/waiting/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /concert/schedule/{concertId} 콘서트 예약 가능 날짜 조회")
    void searchSchedule() throws Exception {
        mockMvc.perform(get("/concert/schedule/{userId}", anyLong()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /concert/seats/{concertId} 콘서트 예약 가능 좌석 조회")
    void searchSeats() throws Exception {
        mockMvc.perform(get("/concert/seats/{concertId}", anyLong()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /concert/seats/booking 콘서트 좌석 예약 요청")
    void bookingSeats() throws Exception {

        Random random = new Random();
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1;

        BookingSeatsRequest bookingSeatsRequest = new BookingSeatsRequest(LocalDate.of(anyInt(), month, day), anyLong());
        String req = objectMapper.writeValueAsString(bookingSeatsRequest);

        mockMvc.perform(post("/concert/seats/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isOk());
    }
}