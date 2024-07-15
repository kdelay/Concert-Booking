package booking.api.waiting.presentation;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static booking.api.waiting.domain.WaitingTokenStatus.DEACTIVATE;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WaitingTokenController.class)
class WaitingTokenControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WaitingTokenService waitingTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        //유저 기본 세팅
        user = User.create(1L, BigDecimal.ZERO);
    }

    @Test
    @DisplayName("POST /waiting/token 유저 대기열 토큰 발급")
    void issue() throws Exception {

        Long userId = 1L;
        Long concertId = 1L;
        Long waitingTokenId = 1L;
        String token = UUID.randomUUID().toString();
        LocalDateTime localDateTime = LocalDateTime.now();

        WaitingToken waitingToken = WaitingToken.create(waitingTokenId, user, token, DEACTIVATE, localDateTime, localDateTime);
        given(waitingTokenService.issue(token, userId, concertId)).willReturn(waitingToken);

        WaitingTokenRequest waitingTokenRequest = new WaitingTokenRequest(userId, concertId);
        String req = objectMapper.writeValueAsString(waitingTokenRequest);

        mockMvc.perform(post("/waiting/token")
                        .header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /waiting/token/{userId} 유저 대기열 토큰 조회")
    void search() throws Exception {

        Long userId = 1L;
        Long waitingTokenId = 1L;
        String token = UUID.randomUUID().toString();
        LocalDateTime localDateTime = LocalDateTime.now();

        WaitingToken waitingToken = WaitingToken.create(waitingTokenId, user, token, DEACTIVATE, localDateTime, localDateTime);
        given(waitingTokenService.search(userId)).willReturn(waitingToken);

        mockMvc.perform(get("/waiting/token/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}