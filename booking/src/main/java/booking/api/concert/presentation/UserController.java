package booking.api.concert.presentation;

import booking.api.concert.presentation.request.ChargeRequest;
import booking.api.concert.presentation.response.ChargeResponse;
import booking.api.concert.presentation.response.SearchAmountResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/user/amount")
public class UserController {

    @PostMapping("/charge/{userId}")
    public ChargeResponse charge(@PathVariable Long userId, @RequestBody ChargeRequest request) {
        return new ChargeResponse(BigDecimal.valueOf(2000));
    }

    @GetMapping("/{userId}")
    public SearchAmountResponse searchAmount(@PathVariable Long userId) {
        return new SearchAmountResponse(BigDecimal.valueOf(2000));
    }
}
