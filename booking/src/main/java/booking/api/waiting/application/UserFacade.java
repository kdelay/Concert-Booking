package booking.api.waiting.application;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    public User charge(Long userId, BigDecimal amount) {
        return userService.charge(userId, amount);
    }

    public User searchAmount(Long userId) {
        return userService.searchAmount(userId);
    }
}
