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
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

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

    public static Reservation create(Long id, Long concertSeatId, Long userId, String concertName, LocalDate concertDate, ReservationStatus reservationStatus, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new Reservation(id, concertSeatId, userId, concertName, concertDate, reservationStatus, createdAt, modifiedAt);
    }

    public void updateReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
}
