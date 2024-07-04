package booking.concert.presentation.controller;

import booking.concert.presentation.request.BookingSeatsRequest;
import booking.concert.presentation.request.WaitingTokenRequest;
import booking.concert.presentation.response.BookingSeatsResponse;
import booking.concert.presentation.response.SearchScheduleResponse;
import booking.concert.presentation.response.SearchSeatsResponse;
import booking.concert.presentation.response.WaitingTokenResponse;
import booking.reservation.domain.ReservationStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/concert")
public class ConcertController {

    @PostMapping("/waiting/token")
    public WaitingTokenResponse waitingToken(@RequestBody WaitingTokenRequest request) {
        return new WaitingTokenResponse("");
    }

    @GetMapping("/schedule/{concertId}")
    public SearchScheduleResponse searchSchedule(@PathVariable Long concertId) {
        return new SearchScheduleResponse(LocalDate.of(2024,7,5));
    }

    @GetMapping("/seats/{concertId}")
    public SearchSeatsResponse searchSeats(@PathVariable Long concertId) {
        return new SearchSeatsResponse(1);
    }

    @PostMapping("/seats/booking")
    public BookingSeatsResponse bookingSeats(@RequestBody BookingSeatsRequest request) {
        return new BookingSeatsResponse(ReservationStatus.RESERVING);
    }
}
