package booking.api.waiting.domain;

import org.springframework.data.repository.query.Param;

public interface WaitingTokenRepository {

    //user
    User findByUserId(Long userId);

    User saveUser(User user);

    //waiting token
    WaitingToken findByToken(String token);

    WaitingToken save(WaitingToken waitingToken);

    Long findActivateTokenSortedByIdDesc();

    WaitingToken findUsingTokenByUserId(@Param("userId") Long userId);
}
