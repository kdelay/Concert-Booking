package booking.api.user.infrastructure;

import booking.api.user.domain.User;
import booking.api.user.domain.UserRepository;
import booking.support.exception.CustomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static booking.api.user.infrastructure.UserEntity.*;
import static booking.support.exception.ErrorCode.USER_IS_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User findByUserId(Long userId) {
        return toDomain(
                jpaUserRepository.findById(userId)
                        .orElseThrow(() -> new CustomNotFoundException(USER_IS_NOT_FOUND,
                                "해당하는 유저가 없습니다. [userId : %d]".formatted(userId)))
        );
    }

    @Override
    public User findLockByUserId(Long userId) {
        return toDomain(
                jpaUserRepository.findLockById(userId)
                        .orElseThrow(() -> new CustomNotFoundException(USER_IS_NOT_FOUND,
                                "해당하는 유저가 없습니다. [userId : %d]".formatted(userId)))
        );
    }

    @Override
    public User saveUser(User user) {
        return toDomain(jpaUserRepository.save(toEntity(user)));
    }

    @Override
    public List<User> findUsers() {
        return toDomainList(jpaUserRepository.findAll());
    }
}
