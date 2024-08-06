package booking.api.concert.domain;

import booking.api.concert.domain.enums.ConcertSeatStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ConcertSeat {

    private final Long id;
    private Long version;
    private final Concert concert;
    private final ConcertSchedule concertSchedule;
    private Long userId;
    private final int seatNumber;
    private final BigDecimal seatPrice;
    private ConcertSeatStatus seatStatus;
    private LocalDateTime modifiedAt;
    private final LocalDateTime expiredAt;

    public ConcertSeat(Long id, Long version, Concert concert, ConcertSchedule concertSchedule, Long userId, int seatNumber, BigDecimal seatPrice, ConcertSeatStatus seatStatus, LocalDateTime modifiedAt, LocalDateTime expiredAt) {
        this.id = id;
        this.version = version;
        this.concert = concert;
        this.concertSchedule = concertSchedule;
        this.userId = userId;
        this.seatNumber = seatNumber;
        this.seatPrice = seatPrice;
        this.seatStatus = seatStatus;
        this.modifiedAt = modifiedAt;
        this.expiredAt = expiredAt;
    }

    public void temporarySeat() {
        this.seatStatus = ConcertSeatStatus.TEMPORARY;
    }

    public void availableSeat() {
        this.seatStatus = ConcertSeatStatus.AVAILABLE;
    }

    public void reservedSeat() {
        this.seatStatus = ConcertSeatStatus.RESERVED;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void updateTime() {
        this.modifiedAt = LocalDateTime.now();
    }
}