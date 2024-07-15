package booking.api.concert.presentation;

import booking.api.concert.domain.ConcertService;
import booking.api.concert.domain.Reservation;
import booking.api.concert.presentation.request.BookingSeatsRequest;
import booking.api.concert.presentation.response.BookingSeatsResponse;
import booking.api.concert.presentation.response.SearchScheduleResponse;
import booking.api.concert.presentation.response.SearchSeatsResponse;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/concert")
public class ConcertController {

    private final ConcertService concertService;

    @GetMapping("/schedules/{concertId}")
    public SearchScheduleResponse searchSchedules(
            @RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long concertId
    ) {
        List<LocalDate> dates = concertService.searchSchedules(token, concertId);
        List<Long> idList = concertService.getConcertScheduleId(concertId);
        return new SearchScheduleResponse(dates, idList);
    }

    @GetMapping("/seats/{concertScheduleId}")
    public List<SearchSeatsResponse> searchSeats(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable long concertScheduleId, @PathParam("concertDate") LocalDate concertDate
    ) {
        List<List<Object>> seatsInfo = concertService.searchSeats(token, concertScheduleId, concertDate);
        return SearchSeatsResponse.of(seatsInfo);
    }

    @PostMapping("/seats/booking")
    public BookingSeatsResponse bookingSeats(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody BookingSeatsRequest request
    ) {
        Reservation reservation = concertService.bookingSeats(token, request.userId(), request.concertScheduleId(), request.concertDate(), request.seatNumberList());
        List<BigDecimal> price = concertService.getConcertSeat(request.userId(), reservation.getConcertSeatId(), request.concertDate(), request.seatNumberList());
        return new BookingSeatsResponse(reservation.getId(), reservation.getConcertSeatId(), request.seatNumberList(), price);
    }
}
