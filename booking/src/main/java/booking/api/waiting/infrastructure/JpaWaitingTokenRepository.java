package booking.api.waiting.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaWaitingTokenRepository extends JpaRepository<WaitingTokenEntity, Long> {

    /**
     * @param userId 유저 PK
     * @return userId 와 동일하고 EXPIRED 상태가 아닌 최신 토큰 정보 반환
     */
    @Query("select wt from WaitingTokenEntity wt where wt.userEntity.id = :userId " +
            "and wt.waitingTokenStatus <> 'EXPIRED' order by wt.createdAt desc")
    Optional<WaitingTokenEntity> findNotExpiredToken(@Param("userId") Long userId);

    /**
     * @return 토큰이 ACTIVATE 상태인 가장 최신 id 번호 반환, 없는 경우 1 반환
     */
    @Query("select coalesce(max(wt.id), 0) from WaitingTokenEntity wt where wt.waitingTokenStatus = 'ACTIVATE' order by wt.id desc limit 1")
    Long findLastActivateWaitingId();

    Optional<WaitingTokenEntity> findByToken(String token);

    /**
     * @return DEACTIVATE 상태인 토큰 전체 조회
     */
    @Query("select wt from WaitingTokenEntity wt where wt.waitingTokenStatus = 'DEACTIVATE' order by wt.id asc limit 3")
    List<WaitingTokenEntity> findDeactivateTokens();
}
