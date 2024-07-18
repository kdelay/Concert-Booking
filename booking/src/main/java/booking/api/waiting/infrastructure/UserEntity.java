package booking.api.waiting.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.Objects;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"user\"")
public class UserEntity {

    @Id
    @Comment("유저 PK")
    private Long id;

    @Column(precision = 7, scale = 0)
    @Comment("잔액")
    private BigDecimal amount;

    @Builder
    public UserEntity(Long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity user = (UserEntity) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
