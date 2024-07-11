package booking.api.waiting.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaWaitingTokenRepository extends JpaRepository<WaitingTokenEntity, Long> {

    @Query("select coalesce(max(wt.id), 1) from WaitingTokenEntity wt where wt.waitingTokenStatus = 'ACTIVATE' order by wt.id desc limit 1")
    Long findActivateTokenSortedByIdDesc();

    @Query("select wt from WaitingTokenEntity wt where wt.userEntity.id = :userId " +
            "and wt.waitingTokenStatus <> 'EXPIRED' order by wt.createdAt desc")
    Optional<WaitingTokenEntity> findUsingTokenByUserId(@Param("userId") Long userId);
}
