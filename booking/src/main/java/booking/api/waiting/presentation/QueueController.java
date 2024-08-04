package booking.api.waiting.presentation;

import booking.api.waiting.domain.QueueService;
import booking.support.Authorization;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class QueueController {

    private final QueueService queueService;

    @PostMapping
    public TokenResponse issueToken() {
        String token = queueService.getNewToken();
        long rank = queueService.getRank(token);
        String ttl = queueService.getTtl(token);
        return new TokenResponse(token, rank, ttl);
    }

    @Authorization
    @GetMapping
    public TokenResponse getTokenInfo(
            HttpServletRequest request
    ) {
        String token = (String) request.getAttribute("token");
        long rank = queueService.getRank(token);
        String ttl = queueService.getTtl(token);
        return new TokenResponse(token, rank, ttl);
    }
}
