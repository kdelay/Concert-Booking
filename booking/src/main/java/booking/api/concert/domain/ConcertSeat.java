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
    private Long userId;
    private int seatNumber;
    private final BigDecimal seatPrice;
    private ConcertSeatStatus seatStatus;
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

    public void updateSeatStatus(ConcertSeatStatus seatStatus) {
        this.seatStatus = seatStatus;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}