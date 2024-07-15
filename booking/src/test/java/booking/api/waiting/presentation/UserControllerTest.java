package booking.api.waiting.presentation;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("PATCH /user/amount/charge/{userId} 잔액 충전")
    void charge() throws Exception {

        Long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(500);

        User user = User.create(userId, BigDecimal.ZERO);
        given(userService.charge(userId, amount)).willReturn(user);

        ChargeRequest request = new ChargeRequest(amount);
        String req = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/user/amount/charge/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /user/amount/{userId} 잔액 조회")
    void searchAmount() throws Exception {

        Long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(500);
        User user = User.create(userId, amount);
        given(userService.searchAmount(userId)).willReturn(user);

        mockMvc.perform(get("/user/amount/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(amount));
    }
}