package booking.api.waiting.interfaces;

import booking.api.waiting.domain.WaitingService;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WaitingController.class)
@AutoConfigureMockMvc
class WaitingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WaitingService waitingService;

    private static final String PATH = "/waiting";

    private RequestPostProcessor requestWithToken(String token) {
        return request -> {
            request.setAttribute("token", token);
            return request;
        };
    }

    @Test
    @DisplayName("POST /waiting 토큰 발급")
    void issueToken() throws Exception {

        String token = "valid-token";
        LocalDateTime remainingTime = LocalDateTime.now();
        WaitingToken waitingToken = WaitingToken.builder()
                .token(token)
                .rank(0L)
                .waitingTokenStatus(WaitingTokenStatus.WAITING)
                .entryAt(remainingTime)
                .build();

        given(waitingService.getWaiting(token)).willReturn(waitingToken);

        mockMvc.perform(post(PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /waiting 토큰 사용 가능 여부 조회")
    void getTokenInfo() throws Exception {

        String token = "valid-token";
        LocalDateTime remainingTime = LocalDateTime.now();
        WaitingToken waitingToken = WaitingToken.builder()
                .token(token)
                .rank(0L)
                .waitingTokenStatus(WaitingTokenStatus.WAITING)
                .entryAt(remainingTime)
                .build();

        given(waitingService.getWaiting(token)).willReturn(waitingToken);

        mockMvc.perform(get("/token")
                    .with(requestWithToken(token))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}