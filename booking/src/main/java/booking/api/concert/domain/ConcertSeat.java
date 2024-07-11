package booking.api.concert.domain;

import booking.api.concert.domain.enums.ConcertSeatStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ConcertSeat {

    private final Long id;
    private final Concert concert;
    private final ConcertSchedule concertSchedule;
    private final Long userId;
    private final int seatNumber;
    private final BigDecimal seatPrice;
    private final ConcertSeatStatus seatStatus;
    private final LocalDateTime modifiedAt;
    private final LocalDateTime expiredAt;

    public ConcertSeat(Long id, Concert concert, ConcertSchedule concertSchedule, Long userId, int seatNumber, BigDecimal seatPrice, ConcertSeatStatus seatStatus, LocalDateTime modifiedAt, LocalDateTime expiredAt) {
        this.id = id;
        this.concert = concert;
        this.concertSchedule = concertSchedule;
        this.userId = userId;
        this.seatNumber = seatNumber;
        this.seatPrice = seatPrice;
        this.seatStatus = seatStatus;
        this.modifiedAt = modifiedAt;
        this.expiredAt = expiredAt;
    }

    public static ConcertSeat create(Long id, Concert concert, ConcertSchedule concertSchedule, Long userId, int seatNumber, BigDecimal seatPrice, ConcertSeatStatus seatStatus, LocalDateTime modifiedAt, LocalDateTime expiredAt) {
        return new ConcertSeat(id, concert, concertSchedule, userId, seatNumber, seatPrice, seatStatus, modifiedAt, expiredAt);
    }
}