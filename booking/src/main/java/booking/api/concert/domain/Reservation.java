package booking.api.concert.domain;

import booking.api.concert.domain.enums.ReservationStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Reservation {

    private final Long id;
    private final Long concertSeatId;
    private final Long userId;
    private final String concertName;
    private final LocalDate concertDate;
    private ReservationStatus reservationStatus;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public Reservation(Long id, Long concertSeatId, Long userId, String concertName, LocalDate concertDate, ReservationStatus reservationStatus, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.concertSeatId = concertSeatId;
        this.userId = userId;
        this.concertName = concertName;
        this.concertDate = concertDate;
        this.reservationStatus = reservationStatus;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Reservation create(long concertSeatId, long userId, String concertName, LocalDate concertDate) {
        return new Reservation(null, concertSeatId, userId, concertName, concertDate,
                ReservationStatus.RESERVING, LocalDateTime.now(), null);
    }

    public void setTotalPrice(BigDecimal seatPrice) {
        this.totalAmount = this.totalAmount.add(seatPrice);
    }

    public void updateReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
}
