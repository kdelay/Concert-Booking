package booking.payment.presentation.controller;

import booking.payment.presentation.request.ChargeRequest;
import booking.payment.presentation.request.PayRequest;
import booking.payment.presentation.response.ChargeResponse;
import booking.payment.presentation.response.PayResponse;
import booking.payment.presentation.response.SearchPaymentResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @PostMapping("/charge")
    public ChargeResponse charge(@RequestBody ChargeRequest request) {
        return new ChargeResponse(1L, 2000);
    }

    @GetMapping("/{userId}")
    public SearchPaymentResponse searchPayment(@PathVariable Long userId) {
        return new SearchPaymentResponse(2000);
    }

    @PostMapping
    public PayResponse pay(@RequestBody PayRequest request) {
        return new PayResponse(1);
    }
}
