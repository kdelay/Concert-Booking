package booking.api.concert.domain.event;

import booking.api.concert.domain.Payment;
import booking.api.concert.domain.Reservation;
import lombok.Getter;

@Getter
public class PaymentSuccessEvent {

    private Reservation reservation;
    private Payment payment;
    private String token;

    public PaymentSuccessEvent() {
    }

    public PaymentSuccessEvent(Reservation reservation, Payment payment, String token) {
        this.reservation = reservation;
        this.payment = payment;
        this.token = token;
    }
}
