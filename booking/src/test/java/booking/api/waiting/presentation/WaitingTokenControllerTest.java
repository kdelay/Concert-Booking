package booking.api.waiting.presentation;

import booking.api.waiting.application.WaitingTokenFacade;
import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.support.WebMvcConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static booking.api.waiting.domain.WaitingTokenStatus.DEACTIVATE;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WaitingTokenController.class)
@Import(WebMvcConfig.class)
@AutoConfigureMockMvc
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
        given(waitingTokenFacade.issueTokenOrSearchWaiting(userId, concertId)).willReturn(waitingToken);
        given(waitingTokenFacade.getRank(waitingToken.getId())).willReturn(0L);

        String req = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(post("/waiting/token")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req)
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(token))
                .andExpect(jsonPath("$.status").value("DEACTIVATE"))
                .andExpect(jsonPath("$.rank").value(1L));
    }

    @Test
    @DisplayName("POST /waiting/token - 헤더 토큰이 없어도 정상적으로 동작해야 한다(신규 토큰 발급)")
    public void emptyHeaderToken() throws Exception {

        WaitingTokenRequest request = new WaitingTokenRequest(1L, 1L);

        String req = objectMapper.writeValueAsString(request);

        User user = new User(1L, BigDecimal.ZERO);
        WaitingToken waitingToken = new WaitingToken(1L, user, "valid-token", DEACTIVATE, LocalDateTime.now(), null);
        given(waitingTokenFacade.issueTokenOrSearchWaiting(anyLong(), anyLong())).willReturn(waitingToken);
        given(waitingTokenFacade.getRank(anyLong())).willReturn(0L);

        mockMvc.perform(post("/waiting/token")
                        .contentType("application/json")
                        .content(req))
                .andExpect(status().isOk());
    }
}