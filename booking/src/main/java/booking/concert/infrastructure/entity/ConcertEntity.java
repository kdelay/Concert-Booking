package booking.concert.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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

    public ConcertEntity(String name, String host) {
        this.name = name;
        this.host = host;
    }
}
