package booking.api.user.interfaces;

import booking.api.user.domain.User;
import booking.api.user.domain.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/amount")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 잔액 충전")
    @PatchMapping("/charge/{userId}")
    public ResponseEntity<ChargeResponse> charge(
            @PathVariable Long userId,
            @RequestBody ChargeRequest request
    ) {
        User user = userService.charge(userId, request.amount());
        return ResponseEntity.ok(new ChargeResponse(user.getAmount()));
    }

    @Operation(summary = "유저 잔액 조회")
    @GetMapping("/{userId}")
    public ResponseEntity<SearchAmountResponse> searchAmount(
            @PathVariable Long userId
    ) {
        User user = userService.searchAmount(userId);
        return ResponseEntity.ok(new SearchAmountResponse(user.getAmount()));
    }
}
