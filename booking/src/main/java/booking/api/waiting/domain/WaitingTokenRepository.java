package booking.api.waiting.domain;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WaitingTokenRepository {

    //user
    User findByUserId(Long userId);

    User saveUser(User user);

    List<User> findUsers();

    //waiting token
    WaitingToken findByToken(String token);

    WaitingToken save(WaitingToken waitingToken);

    Long findLastActivateWaitingId();

    WaitingToken findNotExpiredToken(@Param("userId") Long userId);

    List<WaitingToken> findDeactivateTokens();
}
