package booking.api.waiting.infrastructure;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;

import java.util.List;

public class WaitingTokenMapper {

    public static User userToDomain(UserEntity entity) {
        return User.create(entity.getId(), entity.getAmount());
    }

    public static UserEntity userToEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .amount(user.getAmount())
                .build();
    }

    public static List<User> userToDomainList(List<UserEntity> entities) {
        return entities.stream()
                .map(WaitingTokenMapper::userToDomain)
                .toList();
    }

    public static WaitingToken toDomain(WaitingTokenEntity entity) {
        return new WaitingToken(
                entity.getId(),
                entity.getVersion(),
                UserMapper.toDomain(entity.getUserEntity()),
                entity.getToken(),
                entity.getWaitingTokenStatus(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }

    public static WaitingTokenEntity toEntity(WaitingToken waitingToken) {
        return WaitingTokenEntity.builder()
                .id(waitingToken.getId())
                .version(waitingToken.getVersion())
                .userEntity(userToEntity(waitingToken.getUser()))
                .token(waitingToken.getToken())
                .waitingTokenStatus(waitingToken.getWaitingTokenStatus())
                .createdAt(waitingToken.getCreatedAt())
                .modifiedAt(waitingToken.getModifiedAt())
                .build();
    }

    public static List<WaitingToken> toDomainList(List<WaitingTokenEntity> entities) {
        return entities.stream()
                .map(WaitingTokenMapper::toDomain)
                .toList();
    }
}
