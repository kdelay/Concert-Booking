package booking.api.concert.infrastructure;

import booking.api.concert.domain.enums.ConcertSeatStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concert_seat")
public class ConcertSeatEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("콘서트 좌석 PK")
    private Long id;

    @Version
    @Comment("버전")
    private Long version;

    @ManyToOne
    @JoinColumn(name = "concert_id")
    @Comment("콘서트 PK")
    private ConcertEntity concertEntity;

    @ManyToOne
    @JoinColumn(name = "concert_schedule_id")
    @Comment("콘서트 날짜 PK")
    private ConcertScheduleEntity concertScheduleEntity;

    @Comment("유저 PK")
    private Long userId;

    @Column(nullable = false)
    @Comment("콘서트 좌석 번호")
    private int seatNumber;

    @Column(precision = 7, scale = 0) //1,000,000 단위
    @Comment("콘서트 좌석 금액")
    private BigDecimal seatPrice;

    @Enumerated(EnumType.STRING)
    @Comment("콘서트 좌석 번호 상태")
    private ConcertSeatStatus seatStatus;

    @Comment("상태 변경 시간")
    private LocalDateTime modifiedAt;

    @Comment("좌석 임시 배정 만료 시간")
    private LocalDateTime expiredAt;

    @Builder
    public ConcertSeatEntity(
        Long id,
        Long version,
        ConcertEntity concertEntity,
        ConcertScheduleEntity concertScheduleEntity,
        Long userId,
        int seatNumber,
        BigDecimal seatPrice,
        ConcertSeatStatus seatStatus,
        LocalDateTime modifiedAt,
        LocalDateTime expiredAt
    ) {
        this.id = id;
        this.version = version;
        this.concertEntity = concertEntity;
        this.concertScheduleEntity = concertScheduleEntity;
        this.userId = userId;
        this.seatNumber = seatNumber;
        this.seatPrice = seatPrice;
        this.seatStatus = seatStatus;
        this.modifiedAt = modifiedAt;
        this.expiredAt = expiredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcertSeatEntity concertSeatEntity = (ConcertSeatEntity) o;
        return Objects.equals(id, concertSeatEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}