package booking.api.waiting.presentation;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenService;
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
    WaitingTokenService waitingTokenService;

    @Test
    @DisplayName("POST /waiting/token 발급 및 조회 API 테스트")
    void testIssueTokenOrSearchWaiting() throws Exception {

        long userId = 1L;
        String token = UUID.randomUUID().toString();

        User user = new User(userId, BigDecimal.ZERO);
        WaitingToken waitingToken = new WaitingToken(1L, 0L, user, token, DEACTIVATE, LocalDateTime.now(), null);

        given(waitingTokenService.issueToken(anyLong())).willReturn(waitingToken);
        given(waitingTokenService.getRank(anyLong())).willReturn(0L);

        WaitingTokenRequest waitingTokenRequest = new WaitingTokenRequest(userId);
        String req = objectMapper.writeValueAsString(waitingTokenRequest);

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

    @Test
    @DisplayName("POST /waiting/token - 헤더 토큰이 없어도 정상적으로 동작해야 한다(신규 토큰 발급)")
    public void emptyHeaderToken() throws Exception {

        long userId = 1L;
        String token = "valid-token";

        User user = new User(userId, BigDecimal.ZERO);
        WaitingToken waitingToken = new WaitingToken(1L, 0L, user, token, DEACTIVATE, LocalDateTime.now(), null);

        given(waitingTokenService.issueToken(anyLong())).willReturn(waitingToken);
        given(waitingTokenService.getRank(anyLong())).willReturn(0L);

        WaitingTokenRequest request = new WaitingTokenRequest(userId);
        String req = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/waiting/token")
                        .contentType("application/json")
                        .content(req))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(token))
                .andExpect(jsonPath("$.status").value("DEACTIVATE"))
                .andExpect(jsonPath("$.rank").value(1L));
    }
}