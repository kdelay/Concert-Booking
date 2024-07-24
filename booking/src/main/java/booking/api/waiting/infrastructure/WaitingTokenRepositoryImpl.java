package booking.api.waiting.infrastructure;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenRepository;
import booking.support.exception.CustomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static booking.support.exception.ErrorCode.USER_IS_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class WaitingTokenRepositoryImpl implements WaitingTokenRepository {

    private final JpaUserRepository jpaUserRepository;
    private final JpaWaitingTokenRepository jpaWaitingTokenRepository;

    @Override
    public User findByUserId(Long userId) {
        return WaitingTokenMapper.userToDomain(
                jpaUserRepository.findById(userId)
                        .orElseThrow(() -> new CustomNotFoundException(USER_IS_NOT_FOUND,
                                "해당하는 유저가 없습니다. [userId : %d]".formatted(userId)))
        );
    }

    @Override
    public User saveUser(User user) {
        return WaitingTokenMapper.userToDomain(jpaUserRepository.save(WaitingTokenMapper.userToEntity(user)));
    }

    @Override
    public List<User> findUsers() {
        return WaitingTokenMapper.userToDomainList(jpaUserRepository.findAll());
    }

    @Override
    public WaitingToken findByToken(String token) {
        return WaitingTokenMapper.toDomain(jpaWaitingTokenRepository.findByToken(token).orElseThrow(() -> new IllegalArgumentException("토큰이 없습니다.")));
    }

    @Override
    public WaitingToken save(WaitingToken waitingToken) {
        return WaitingTokenMapper.toDomain(
                jpaWaitingTokenRepository.save(WaitingTokenMapper.toEntity(waitingToken))
        );
    }

    @Override
    public Long findLastActivateWaitingId() {
        return jpaWaitingTokenRepository.findLastActivateWaitingId().describeConstable().orElse(0L);
    }

    @Override
    public WaitingToken findNotExpiredToken(Long userId) {
        WaitingTokenEntity waitingTokenEntity = jpaWaitingTokenRepository.findNotExpiredToken(userId).orElse(null);
        if (waitingTokenEntity == null) return null;
        return WaitingTokenMapper.toDomain(waitingTokenEntity);
    }

    @Override
    public List<WaitingToken> findDeactivateTokens() {
        return WaitingTokenMapper.toDomainList(jpaWaitingTokenRepository.findAll());
    }
}
