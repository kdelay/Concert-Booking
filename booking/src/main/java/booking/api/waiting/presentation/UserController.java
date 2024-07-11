package booking.api.waiting.presentation;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/amount")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/charge/{userId}")
    public ChargeResponse charge(@PathVariable Long userId, @RequestBody ChargeRequest request) {
        User user = userService.charge(userId, request.amount());
        return new ChargeResponse(user.getAmount());
    }

    @GetMapping("/{userId}")
    public SearchAmountResponse searchAmount(@PathVariable Long userId) {
        User user = userService.searchAmount(userId);
        return new SearchAmountResponse(user.getAmount());
    }
}