package booking.api.concert.presentation;

import booking.api.concert.domain.*;
import booking.api.concert.presentation.request.BookingSeatsRequest;
import booking.api.concert.presentation.request.PayRequest;
import booking.api.concert.presentation.response.BookingSeatsResponse;
import booking.api.concert.presentation.response.PayResponse;
import booking.api.concert.presentation.response.SearchScheduleResponse;
import booking.api.concert.presentation.response.SearchSeatsResponse;
import booking.support.Authorization;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/concert")
public class ConcertController {

    private final ConcertService concertService;

    @GetMapping("/list")
    public List<Concert> getList() {
        return concertService.getConcertsWithCache();
    }

    @Authorization
    @GetMapping("/schedules/{concertId}")
    public SearchScheduleResponse getSchedules(@PathVariable Long concertId) {
        List<ConcertSchedule> concertSchedules = concertService.getSchedules(concertId);
        return new SearchScheduleResponse(
                concertService.getConcertScheduleDates(concertSchedules),
                concertService.getConcertScheduleIds(concertSchedules)
        );
    }

    @Authorization
    @GetMapping("/seats/{concertScheduleId}")
    public List<SearchSeatsResponse> getSeats(
            @PathVariable long concertScheduleId, @PathParam("concertDate") LocalDate concertDate
    ) {
        return SearchSeatsResponse.of(concertService.getSeats(concertScheduleId, concertDate));
    }

    @Authorization
    @PostMapping("/seats/booking")
    public List<BookingSeatsResponse> bookingSeats(
            HttpServletRequest request,
            @RequestBody BookingSeatsRequest seatsRequest
    ) {
        String token = (String) request.getAttribute("token");
        List<Reservation> reservations = concertService.bookingSeats(
                seatsRequest.userId(), seatsRequest.concertScheduleId(),
                seatsRequest.concertDate(), seatsRequest.seatNumberList(), token);
        return BookingSeatsResponse.of(reservations);
    }

    @Authorization
    @PostMapping("/payment")
    public PayResponse pay(
            HttpServletRequest request,
            @RequestBody PayRequest payRequest
    ) {
        String token = (String) request.getAttribute("token");
        ConcertSeat concertSeat = concertService.pay(payRequest.concertSeatId(), payRequest.reservationId(), token);
        return new PayResponse(concertSeat.getSeatNumber());
    }
}
