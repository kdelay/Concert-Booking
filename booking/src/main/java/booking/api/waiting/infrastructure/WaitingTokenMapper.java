package booking.api.waiting.infrastructure;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;

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

    public static WaitingToken toDomain(WaitingTokenEntity entity) {
        return new WaitingToken(
                entity.getId(),
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
                .userEntity(userToEntity(waitingToken.getUser()))
                .token(waitingToken.getToken())
                .waitingTokenStatus(waitingToken.getWaitingTokenStatus())
                .createdAt(waitingToken.getCreatedAt())
                .modifiedAt(waitingToken.getModifiedAt())
                .build();
    }
}
