package booking.api.concert.infrastructure.db;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.Objects;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concert")
public class ConcertEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("콘서트 PK")
    private Long id;

    @Column(length = 20, nullable = false)
    @Comment("콘서트 이름")
    private String name;

    @Column(length = 10, nullable = false)
    @Comment("콘서트 주최자")
    private String host;

    @Builder
    public ConcertEntity(Long id, String name, String host) {
        this.id = id;
        this.name = name;
        this.host = host;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcertEntity concertEntity = (ConcertEntity) o;
        return Objects.equals(id, concertEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
