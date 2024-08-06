package booking.support;

import booking.api.waiting.interfaces.TokenResponse;
import booking.support.config.WebMvcConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(WebMvcConfig.class)
@ActiveProfiles("test")
class WaitingInterceptorTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("GET /waiting - 헤더 토큰 있음")
    public void testAuthorizationHeaderPresent() {

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "Bearer " + "valid-token");
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<TokenResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/waiting",
                HttpMethod.GET,
                request,
                TokenResponse.class);

        //응답 상태 코드 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        //응답 본문 검증
        TokenResponse responseBody = responseEntity.getBody();
        assertThat(responseBody).isNotNull();

        //응답 헤더에서 'Authorization' 토큰 값 추출 및 검증
        List<String> auth = responseEntity.getHeaders().get(AUTHORIZATION);
        assertThat(auth).isNotNull();
    }

    @Test
    @DisplayName("GET /waiting - 헤더 토큰 없음")
    public void testAuthorizationHeaderAbsent() {

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<TokenResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/waiting",
                HttpMethod.GET,
                request,
                TokenResponse.class);

        //응답 상태 코드 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        //응답 본문 검증
        TokenResponse responseBody = responseEntity.getBody();
        assertThat(responseBody).isNotNull();

        //응답 헤더에서 'Authorization' 토큰 값 추출 및 검증
        List<String> auth = responseEntity.getHeaders().get(AUTHORIZATION);
        assertThat(auth).isNull();
    }
}