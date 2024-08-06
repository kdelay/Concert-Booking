package booking.api.user.infrastructure;

import booking.api.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"user\"")
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public static User toDomain(UserEntity entity) {
        return User.create(entity.getId(), entity.getAmount());
    }

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .amount(user.getAmount())
                .build();
    }

    public static List<User> toDomainList(List<UserEntity> entities) {
        return entities.stream()
                .map(UserEntity::toDomain)
                .toList();
    }
}
