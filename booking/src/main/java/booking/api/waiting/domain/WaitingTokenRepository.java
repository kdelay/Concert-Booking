package booking.api.waiting.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WaitingTokenRepository {

    //user
    User findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    User findLockByUserId(Long userId);

    User saveUser(User user);

    List<User> findUsers();

    //waiting token
    WaitingToken save(WaitingToken waitingToken);

    Long findLastActivateWaitingId();

    WaitingToken findNotExpiredToken(@Param("userId") Long userId);

    List<WaitingToken> findDeactivateTokens();

    List<WaitingToken> saveAll(List<WaitingToken> waitingTokenList);

    WaitingToken findWaitingByUserId(Long userId);
}
