package booking.api.concert.presentation;

import booking.api.concert.domain.enums.ReservationStatus;
import booking.api.concert.presentation.request.BookingSeatsRequest;
import booking.api.concert.presentation.response.BookingSeatsResponse;
import booking.api.concert.presentation.response.SearchScheduleResponse;
import booking.api.concert.presentation.response.SearchSeatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static booking.api.concert.domain.enums.ConcertSeatStatus.TEMPORARY;
import static booking.api.concert.domain.enums.ReservationStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/concerts")
public class ConcertController {

    @GetMapping("/schedules/{concertId}")
    public SearchScheduleResponse searchSchedule(@PathVariable Long concertId) {
        return new SearchScheduleResponse(LocalDate.of(2024,7,5), 1);
    }

    @GetMapping("/seats/{concertId}/{concertScheduleId}")
    public SearchSeatsResponse searchSeats(@PathVariable Long concertId, @PathVariable long concertScheduleId) {
        return new SearchSeatsResponse(1, BigDecimal.valueOf(1), TEMPORARY);
    }

    @PostMapping("/seats/booking")
    public BookingSeatsResponse bookingSeats(@RequestBody BookingSeatsRequest request) {
        return new BookingSeatsResponse(1, 1, 1, BigDecimal.valueOf(1), RESERVING);
    }
}
