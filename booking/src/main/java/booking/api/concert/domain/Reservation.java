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

    /**
     * @param concertSeatId 콘서트 좌석 PK
     * @param userId 유저 PK
     * @param concertName 콘서트 이름
     * @param concertDate 콘서트 날짜
     * @return 새로운 예약 데이터 추가
     */
    public static Reservation create(long concertSeatId, long userId, String concertName, LocalDate concertDate) {
        return new Reservation(null, concertSeatId, userId, concertName, concertDate,
                ReservationStatus.RESERVING, LocalDateTime.now(), null);
    }

    /**
     * 예약 총 금액
     * @param seatPrice 좌석 금액
     */
    public void setTotalPrice(BigDecimal seatPrice) {
        this.totalAmount = this.totalAmount.add(seatPrice);
    }

    /**
     * 예약 상태 변경
     * @param reservationStatus 예약 상태
     */
    public void updateReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
}
