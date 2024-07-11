package booking.api.concert.presentation;

import booking.api.concert.domain.ConcertService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @DisplayName("GET /concert/schedules/{concertId} 콘서트 예약 가능 날짜 조회")
    void searchSchedules() throws Exception {

        Long concertId = 1L;
        String token = UUID.randomUUID().toString();

        List<LocalDate> dates = List.of(LocalDate.parse("2024-07-10"), LocalDate.parse("2024-07-11"));
        List<Long> idList = List.of(1L, 2L);
        given(concertService.searchSchedules(token, concertId)).willReturn(dates);
        given(concertService.getConcertScheduleId(concertId)).willReturn(idList);

        mockMvc.perform(get("/concert/schedules/{concertId}", concertId)
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void searchSeats() {
    }

    @Test
    void bookingSeats() {
    }
}