package booking.waiting.infrastructure.entity;

import booking.waiting.domain.enums.WaitingTokenStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

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
}
