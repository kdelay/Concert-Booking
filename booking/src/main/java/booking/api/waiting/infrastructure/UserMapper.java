package booking.api.waiting.infrastructure;

import booking.api.waiting.domain.User;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        return User.create(entity.getId(), entity.getAmount());
    }

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .amount(user.getAmount())
                .build();
    }
}
