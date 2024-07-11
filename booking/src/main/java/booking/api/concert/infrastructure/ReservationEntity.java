package booking.api.concert.infrastructure;

import booking.api.concert.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation")
public class ReservationEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("콘서트 예약 PK")
    private Long id;

    @Column(nullable = false)
    @Comment("콘서트 좌석 PK")
    private Long concertSeatId;

    @Column(nullable = false)
    @Comment("유저 PK")
    private Long userId;

    @Column(length = 20, nullable = false)
    @Comment("콘서트 이름")
    private String concertName;

    @Column(nullable = false)
    @Comment("콘서트 날짜")
    private LocalDate concertDate;

    @Enumerated(EnumType.STRING)
    @Comment("예약 상태")
    private ReservationStatus reservationStatus;

    @Comment("콘서트 예약 요청 시간")
    private LocalDateTime createdAt;

    @Comment("상태 변경 시간")
    private LocalDateTime modifiedAt;

    @Builder
    public ReservationEntity(
        Long id,
        Long concertSeatId,
        Long userId,
        String concertName,
        LocalDate concertDate,
        ReservationStatus reservationStatus,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.concertSeatId = concertSeatId;
        this.userId = userId;
        this.concertName = concertName;
        this.concertDate = concertDate;
        this.reservationStatus = reservationStatus;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationEntity reservationEntity = (ReservationEntity) o;
        return Objects.equals(id, reservationEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
