package booking.api.concert.infrastructure;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.Objects;

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

    @Builder
    public ConcertScheduleEntity(Long id, ConcertEntity concertEntity, LocalDate concertDate) {
        this.id = id;
        this.concertEntity = concertEntity;
        this.concertDate = concertDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcertScheduleEntity concertScheduleEntity = (ConcertScheduleEntity) o;
        return Objects.equals(id, concertScheduleEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}