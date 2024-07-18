package booking.api.concert.presentation;

import booking.api.concert.application.ConcertFacade;
import booking.api.concert.domain.Concert;
import booking.api.concert.domain.ConcertSchedule;
import booking.api.concert.domain.Reservation;
import booking.api.concert.presentation.request.BookingSeatsRequest;
import booking.api.concert.presentation.response.BookingSeatsResponse;
import booking.api.concert.presentation.response.SearchScheduleResponse;
import booking.api.concert.presentation.response.SearchSeatsResponse;
import booking.support.Authorization;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/concert")
public class ConcertController {

    private final ConcertFacade concertFacade;

    @GetMapping("/list")
    public List<Concert> searchList() {
        return concertFacade.searchList();
    }

    @Authorization
    @GetMapping("/schedules/{concertId}")
    public SearchScheduleResponse searchSchedules(@PathVariable Long concertId) {
        List<ConcertSchedule> concertSchedules = concertFacade.searchSchedules(concertId);
        return new SearchScheduleResponse(
                concertFacade.getConcertScheduleDates(concertSchedules),
                concertFacade.getConcertScheduleId(concertSchedules)
        );
    }

    @Authorization
    @GetMapping("/seats/{concertScheduleId}")
    public List<SearchSeatsResponse> searchSeats(
            @PathVariable long concertScheduleId, @PathParam("concertDate") LocalDate concertDate
    ) {
        return SearchSeatsResponse.of(concertFacade.searchSeats(concertScheduleId, concertDate));
    }

    @Authorization
    @PostMapping("/seats/booking")
    public List<BookingSeatsResponse> bookingSeats(@RequestBody BookingSeatsRequest request) {
        List<Reservation> reservations = concertFacade.bookingSeats(request.userId(), request.concertScheduleId(), request.concertDate(), request.seatNumberList());
        return BookingSeatsResponse.of(reservations);
    }
}
