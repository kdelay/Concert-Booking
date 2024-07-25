package booking.api.waiting.infrastructure;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenRepository;
import booking.support.exception.CustomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static booking.api.waiting.infrastructure.WaitingTokenMapper.*;
import static booking.support.exception.ErrorCode.USER_IS_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class WaitingTokenRepositoryImpl implements WaitingTokenRepository {

    private final JpaUserRepository jpaUserRepository;
    private final JpaWaitingTokenRepository jpaWaitingTokenRepository;

    @Override
    public User findByUserId(Long userId) {
        return userToDomain(
                jpaUserRepository.findById(userId)
                        .orElseThrow(() -> new CustomNotFoundException(USER_IS_NOT_FOUND,
                                "해당하는 유저가 없습니다. [userId : %d]".formatted(userId)))
        );
    }

    @Override
    public User findLockByUserId(Long userId) {
        return userToDomain(
                jpaUserRepository.findById(userId)
                        .orElseThrow(() -> new CustomNotFoundException(USER_IS_NOT_FOUND,
                                "해당하는 유저가 없습니다. [userId : %d]".formatted(userId)))
        );
    }

    @Override
    public User saveUser(User user) {
        return userToDomain(jpaUserRepository.save(userToEntity(user)));
    }

    @Override
    public List<User> findUsers() {
        return userToDomainList(jpaUserRepository.findAll());
    }

    @Override
    public WaitingToken save(WaitingToken waitingToken) {
        return toDomain(
                jpaWaitingTokenRepository.save(toEntity(waitingToken))
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
        return toDomain(waitingTokenEntity);
    }

    @Override
    public List<WaitingToken> findDeactivateTokens() {
        return toDomainList(jpaWaitingTokenRepository.findAll());
    }

    @Override
    public List<WaitingToken> saveAll(List<WaitingToken> waitingTokenList) {
        return toDomainList(jpaWaitingTokenRepository.saveAll(waitingTokenList.stream()
                .map(WaitingTokenMapper::toEntity).toList()));
    }

    @Override
    public WaitingToken findWaitingByUserId(Long userId) {
        return toDomain(jpaWaitingTokenRepository.findByUserId(userId));
    }
}
