package booking.balance.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "balance_history")
public class BalanceHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("잔액 히스토리 PK")
    private Long id;

    @Column(nullable = false)
    @Comment("유저 PK")
    private Long userId;

    @Column(precision = 7, scale = 0)
    @Comment("잔액")
    private BigDecimal amount;

    @Comment("생성 날짜")
    private LocalDateTime createdAt;

    @Comment("수정 날짜")
    private LocalDateTime modifiedAt;
}
