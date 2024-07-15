package booking.api.waiting.presentation;

import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/waiting/token")
public class WaitingTokenController {

    private final WaitingTokenService waitingTokenService;

    @PostMapping
    public WaitingTokenResponse issue(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody WaitingTokenRequest request) {
        WaitingToken result = waitingTokenService.issue(token, request.userId(), request.concertId());
        long rank = waitingTokenService.getRank(result.getId());
        return WaitingTokenResponse.of(result, rank+1);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WaitingToken> search(@PathVariable Long userId) {
        return ResponseEntity.ok(waitingTokenService.search(userId));
    }
}