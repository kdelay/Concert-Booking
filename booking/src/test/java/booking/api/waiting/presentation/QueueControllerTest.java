package booking.api.waiting.presentation;

import booking.api.waiting.domain.QueueService;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QueueController.class)
@AutoConfigureMockMvc
class QueueControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    QueueService queueService;

    private RequestPostProcessor requestWithToken(String token) {
        return request -> {
            request.setAttribute("token", token);
            return request;
        };
    }

    @Test
    @DisplayName("POST /token 토큰 발급")
    void issueToken() throws Exception {

        String token = "valid-token";
        long rank = 0L;
        String ttl = "0분 0초";

        given(queueService.getNewToken()).willReturn(token);
        given(queueService.getRank(token)).willReturn(rank);
        given(queueService.getTtl(token)).willReturn(ttl);

        mockMvc.perform(post("/token")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.rank").value(rank))
                .andExpect(jsonPath("$.ttl").value(ttl));
    }

    @Test
    @DisplayName("GET /token 토큰 사용 가능 여부 조회")
    void getTokenInfo() throws Exception {

        String token = "valid-token";
        long rank = 0L;
        String ttl = "0분 0초";

        given(queueService.getRank(token)).willReturn(rank);
        given(queueService.getTtl(token)).willReturn(ttl);

        mockMvc.perform(get("/token")
                    .with(requestWithToken(token))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.rank").value(rank))
                .andExpect(jsonPath("$.ttl").value(ttl));
    }
}