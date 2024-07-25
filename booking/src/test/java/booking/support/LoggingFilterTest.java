package booking.support;

import booking.api.waiting.presentation.WaitingTokenRequest;
import booking.api.waiting.presentation.WaitingTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(LoggingFilter.class)
@ActiveProfiles("test")
class LoggingFilterTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("POST /waiting/token 대기열 토큰 발급 및 조회 - Filter 적용")
    public void filterWaitingToken() {

        WaitingTokenRequest request = new WaitingTokenRequest(1L);

        ResponseEntity<WaitingTokenResponse> responseEntity = restTemplate.postForEntity(
                "http://localhost:" + port + "/waiting/token",
                createHttpEntityWithToken(request, "valid-token"),
                WaitingTokenResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        WaitingTokenResponse responseBody = responseEntity.getBody();
        assertThat(responseBody).isNotNull();
    }

    private HttpEntity<WaitingTokenRequest> createHttpEntityWithToken(WaitingTokenRequest request, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, "Bearer " + token);
        return new HttpEntity<>(request, headers);
    }
}