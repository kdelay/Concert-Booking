package booking.support;

import booking.api.waiting.application.WaitingTokenFacade;
import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenStatus;
import booking.api.waiting.presentation.WaitingTokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(LoggingFilter.class)
class LoggingFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WaitingTokenFacade waitingTokenFacade;

    @Test
    @DisplayName("POST /waiting/token 대기열 토큰 발급 및 조회 - Filter 적용")
    public void filterWaitingToken() throws Exception {

        String token = "valid-token";

        User user = new User(1L, BigDecimal.ZERO);
        WaitingToken waitingToken = new WaitingToken(1L, user, token, WaitingTokenStatus.DEACTIVATE, LocalDateTime.now(), null);
        given(waitingTokenFacade.issueTokenOrSearchWaiting(1L, 1L)).willReturn(waitingToken);
        given(waitingTokenFacade.getRank(1L)).willReturn(0L);

        WaitingTokenRequest waitingTokenRequest = new WaitingTokenRequest(1L, 1L);
        String req = objectMapper.writeValueAsString(waitingTokenRequest);

        mockMvc.perform(post("/waiting/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

}