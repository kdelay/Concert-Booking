package booking.api.concert.interfaces;

import booking.api.concert.domain.*;
import booking.api.concert.interfaces.request.BookingSeatsRequest;
import booking.api.concert.interfaces.request.PayRequest;
import booking.api.concert.interfaces.response.BookingSeatsResponse;
import booking.api.concert.interfaces.response.PayResponse;
import booking.api.concert.interfaces.response.SearchScheduleResponse;
import booking.api.concert.interfaces.response.SearchSeatsResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/concert")
public class ConcertController {

    private final ConcertService concertService;

    @Operation(summary = "콘서트 목록 조회")
    @GetMapping("/list")
    public ResponseEntity<List<Concert>> getList() {
        return ResponseEntity.ok(concertService.getConcertsWithCache());
    }

    @Operation(summary = "콘서트 일정 목록 조회")
    @GetMapping("/schedules/{concertId}")
    public ResponseEntity<SearchScheduleResponse> getSchedules(
            @PathVariable Long concertId
    ) {
        List<ConcertSchedule> concertSchedules = concertService.getSchedulesWithCache(concertId);
        return ResponseEntity.ok(new SearchScheduleResponse(
                concertService.getConcertScheduleDates(concertSchedules),
                concertService.getConcertScheduleIds(concertSchedules)
        ));
    }

    @Operation(summary = "콘서트 좌석 목록 조회")
    @GetMapping("/seats/{concertScheduleId}")
    public ResponseEntity<List<SearchSeatsResponse>> getSeats(
            @PathVariable long concertScheduleId,
            @PathParam("concertDate") LocalDate concertDate
    ) {
        return ResponseEntity.ok(SearchSeatsResponse.of(concertService.getSeats(concertScheduleId, concertDate)));
    }

    @Operation(summary = "콘서트 예약 요청")
    @PostMapping("/seats/booking")
    public ResponseEntity<List<BookingSeatsResponse>> bookingSeats(
            @RequestBody BookingSeatsRequest seatsRequest,
            @RequestHeader(required = false, name = "Authorization") String token
    ) {
        List<Reservation> reservations = concertService.bookingSeats(
                seatsRequest.userId(), seatsRequest.concertScheduleId(),
                seatsRequest.concertDate(), seatsRequest.seatNumberList(), token);
        return ResponseEntity.ok(BookingSeatsResponse.of(reservations));
    }

    @Operation(summary = "콘서트 좌석 결제")
    @PostMapping("/payment")
    public ResponseEntity<PayResponse> pay(
            @RequestBody PayRequest payRequest,
            @RequestHeader(required = false, name = "Authorization") String token
    ) {
        ConcertSeat concertSeat = concertService.pay(payRequest.concertSeatId(), payRequest.reservationId(), token);
        return ResponseEntity.ok(new PayResponse(concertSeat.getSeatNumber()));
    }
}
