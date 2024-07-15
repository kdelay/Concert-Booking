package booking.api.waiting.infrastructure;

import booking.api.concert.infrastructure.ConcertMapper;
import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WaitingTokenRepositoryImpl implements WaitingTokenRepository {

    private final JpaUserRepository jpaUserRepository;
    private final JpaWaitingTokenRepository jpaWaitingTokenRepository;

    @Override
    public User findByUserId(Long userId) {
        return WaitingTokenMapper.userToDomain(
                jpaUserRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User - userId not found"))
        );
    }

    @Override
    public User saveUser(User user) {
        return WaitingTokenMapper.userToDomain(jpaUserRepository.save(WaitingTokenMapper.userToEntity(user)));
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
    public Long findActivateTokenSortedByIdDesc() {
        return jpaWaitingTokenRepository.findActivateTokenSortedByIdDesc().describeConstable().orElse(null);
    }

    @Override
    public WaitingToken findUsingTokenByUserId(Long userId) {
        return WaitingTokenMapper.toDomain(
                jpaWaitingTokenRepository.findUsingTokenByUserId(userId).orElseThrow(() -> new EntityNotFoundException("WaitingToken - userId not found"))
        );
    }
}
