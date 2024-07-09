package booking.balance.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "balance")
public class BalanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("잔액 PK")
    private Long id;

    @Column(nullable = false)
    @Comment("유저 PK")
    private Long userId;

    @Column(precision = 7, scale = 0)
    @Comment("잔액")
    private BigDecimal amount = BigDecimal.valueOf(0);
}
