package booking.payment.presentation.controller;

import booking.payment.presentation.request.PayRequest;
import booking.payment.presentation.response.PayResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @PostMapping
    public PayResponse pay(@RequestBody PayRequest request) {
        return new PayResponse(1);
    }
}
