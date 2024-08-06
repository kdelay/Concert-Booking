package booking.api.user.domain;

import java.util.List;

public interface UserRepository {

    //user
    User findByUserId(Long userId);

    User findLockByUserId(Long userId);

    User saveUser(User user);

    List<User> findUsers();
}
