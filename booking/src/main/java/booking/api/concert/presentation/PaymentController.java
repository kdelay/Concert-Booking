package booking.api.concert.presentation;

import booking.api.concert.presentation.request.PayRequest;
import booking.api.concert.presentation.response.PayResponse;
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
