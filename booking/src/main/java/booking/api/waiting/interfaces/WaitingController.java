package booking.api.waiting.interfaces;

import booking.api.waiting.domain.WaitingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/waiting")
public class WaitingController {

    private final WaitingService waitingService;

    @Operation(summary = "대기열 토큰 등록 및 조회")
    @PostMapping
    public ResponseEntity<TokenResponse> issueToken(
            @RequestHeader(required = false, name = "Authorization") String token
    ) {
        return ResponseEntity.ok(TokenResponse.create(waitingService.getWaiting(token)));
    }
}
