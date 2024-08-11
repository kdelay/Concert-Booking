package booking.api.concert.domain.event;

import booking.api.concert.domain.Payment;
import booking.api.concert.domain.Reservation;
import lombok.Getter;

@Getter
public class PaymentSuccessEvent {

    private final Reservation reservation;
    private final Payment payment;
    private final String token;

    public PaymentSuccessEvent(Reservation reservation, Payment payment, String token) {
        this.reservation = reservation;
        this.payment = payment;
        this.token = token;
    }
}
