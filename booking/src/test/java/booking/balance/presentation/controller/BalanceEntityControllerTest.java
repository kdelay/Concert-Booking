package booking.balance.presentation.controller;

import booking.payment.presentation.request.ChargeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BalanceController.class)
class BalanceEntityControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /balance/charge 잔액 충전")
    void charge() throws Exception {

        ChargeRequest chargeRequest = new ChargeRequest(anyInt());
        String req = objectMapper.writeValueAsString(chargeRequest);

        mockMvc.perform(post("/balance/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /balance/{userId} 잔액 조회")
    void searchAmount() throws Exception {
        mockMvc.perform(get("/balance/{userId}", anyLong()))
                .andExpect(status().isOk());
    }
}