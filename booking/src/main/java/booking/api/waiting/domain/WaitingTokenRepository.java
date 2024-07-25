package booking.api.waiting.domain;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WaitingTokenRepository {

    //user
    User findByUserId(Long userId);

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
