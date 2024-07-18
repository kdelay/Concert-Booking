package booking.api.concert.presentation;

import booking.api.concert.application.ConcertFacade;
import booking.api.concert.domain.Concert;
import booking.api.concert.domain.ConcertSchedule;
import booking.api.concert.domain.ConcertSeat;
import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.concert.presentation.request.PayRequest;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ConcertFacade concertFacade;

    @Test
    @DisplayName("POST /payment 결제")
    void pay() throws Exception {

        Concert concert = Concert.create(1L, "A 콘서트", "A");
        ConcertSchedule concertSchedule = ConcertSchedule.create(1L, concert, LocalDate.parse("2024-07-10"));
        ConcertSeat concertSeat = new ConcertSeat(1L, concert, concertSchedule, 1L, 1, BigDecimal.valueOf(1000), ConcertSeatStatus.TEMPORARY, null, null);
        given(concertFacade.pay(1L, 1L)).willReturn(concertSeat);

        PayRequest payRequest = new PayRequest(1L, 1L);
        String req = objectMapper.writeValueAsString(payRequest);

        mockMvc.perform(post("/payment")
                .header("Authorization", "valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}