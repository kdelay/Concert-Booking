package booking.api.concert.domain;

public interface MockApiClient {

    boolean sendSlack(Reservation reservation, Payment payment);
}
