package booking.api.concert.domain;

import booking.api.concert.domain.enums.ReservationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Reservation {

    private Long id;
    private Long concertSeatId;
    private Long userId;
    private String concertName;
    private LocalDate concertDate;
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

    public void fiveMinuteAgo() {
        this.createdAt = LocalDateTime.now().minusMinutes(6);
    }

    public void updateTime() {
        this.modifiedAt = LocalDateTime.now();
    }
}
