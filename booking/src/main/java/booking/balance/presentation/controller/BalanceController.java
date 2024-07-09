package booking.balance.presentation.controller;

import booking.payment.presentation.request.ChargeRequest;
import booking.payment.presentation.response.ChargeResponse;
import booking.payment.presentation.response.SearchAmountResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    @PostMapping("/charge")
    public ChargeResponse charge(@RequestBody ChargeRequest request) {
        return new ChargeResponse(1L, 2000);
    }

    @GetMapping("/{userId}")
    public SearchAmountResponse searchAmount(@PathVariable Long userId) {
        return new SearchAmountResponse(2000);
    }
}
