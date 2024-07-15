package booking.api.concert.presentation;

import booking.api.concert.domain.ConcertSeat;
import booking.api.concert.domain.ConcertService;
import booking.api.concert.presentation.request.PayRequest;
import booking.api.concert.presentation.response.PayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final ConcertService concertService;

    @PostMapping
    public PayResponse pay(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody PayRequest request
    ) {
        ConcertSeat concertSeat = concertService.pay(token, request.concertSeatId(), request.reservationId());
        return new PayResponse(concertSeat.getSeatNumber());
    }
}
