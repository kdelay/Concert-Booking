package booking.api.waiting.presentation;

import booking.api.waiting.application.WaitingTokenFacade;
import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WaitingTokenController.class)
class WaitingTokenControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WaitingTokenFacade waitingTokenFacade;

    @Test
    @DisplayName("POST /waiting/token 발급 및 조회 API 테스트")
    void testIssueTokenOrSearchWaiting() throws Exception {

        long userId = 1L;
        long concertId = 1L;

        String token = UUID.randomUUID().toString();
        WaitingTokenRequest request = new WaitingTokenRequest(userId, concertId);

        User user = new User(userId, BigDecimal.ZERO);
        WaitingToken waitingToken = new WaitingToken(1L, user, token, DEACTIVATE, LocalDateTime.now(), null);
        given(waitingTokenFacade.issueTokenOrSearchWaiting(token, userId, concertId)).willReturn(waitingToken);
        given(waitingTokenFacade.getRank(waitingToken.getId())).willReturn(0L);

        String req = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(post("/waiting/token")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(token))
                .andExpect(jsonPath("$.status").value("DEACTIVATE"))
                .andExpect(jsonPath("$.rank").value(1L));
    }
}