package booking.waiting.infrastructure.entity;

import booking.waiting.domain.enums.WaitingTokenStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WaitingToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    private WaitingTokenStatus waitingTokenStatus;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
