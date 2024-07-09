package booking.reservation.infrastructure.entity;

import booking.reservation.domain.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
}
