package booking.api.waiting.infrastructure;

import booking.api.waiting.domain.WaitingTokenStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "waiting_token")
public class WaitingTokenEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("대기열 PK")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Comment("유저 PK")
    private UserEntity userEntity;

    @Column(unique = true, nullable = false)
    @Comment("토큰 정보(uuid)")
    private String token;

    @Enumerated(EnumType.STRING)
    @Comment("대기열 토큰 상태")
    private WaitingTokenStatus waitingTokenStatus;

    @Comment("대기열 토큰 요청 시간")
    private LocalDateTime createdAt;

    @Comment("상태 변경 시간")
    private LocalDateTime modifiedAt;

    @Builder
    public WaitingTokenEntity(
            Long id,
            UserEntity userEntity,
            String token,
            WaitingTokenStatus waitingTokenStatus,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.userEntity = userEntity;
        this.token = token;
        this.waitingTokenStatus = waitingTokenStatus;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaitingTokenEntity waitingTokenEntity = (WaitingTokenEntity) o;
        return Objects.equals(id, waitingTokenEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}