package booking.api.waiting.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static booking.api.waiting.domain.WaitingTokenStatus.ACTIVATE;

@RestController
public class WaitingController {

    @PostMapping("/waiting/token")
    public WaitingTokenResponse waitingToken(@RequestBody WaitingTokenRequest request) {
        return new WaitingTokenResponse("", ACTIVATE,1);
    }
}
