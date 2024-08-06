package booking.api.concert.domain;

import booking.api.concert.domain.enums.ReservationStatus;
import lombok.Getter;

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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

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

    public void canceledReservation() {
        this.reservationStatus = ReservationStatus.CANCELED;
    }

    public void reservedReservation() {
        this.reservationStatus = ReservationStatus.RESERVED;
    }

    public void twoMinutesAgo() {
        this.createdAt = LocalDateTime.now().minusMinutes(2);
    }

    public void updateTime() {
        this.modifiedAt = LocalDateTime.now();
    }
}
