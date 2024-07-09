package booking.concert.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concert_schedule")
public class ConcertScheduleEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("콘서트 날짜 PK")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "concert_id")
    @Comment("콘서트 PK")
    private ConcertEntity concertEntity;

    @Column(nullable = false)
    @Comment("콘서트 날짜")
    private LocalDate concertDate;

    public ConcertScheduleEntity(ConcertEntity concertEntity, LocalDate concertDate) {
        this.concertEntity = concertEntity;
        this.concertDate = concertDate;
    }
}