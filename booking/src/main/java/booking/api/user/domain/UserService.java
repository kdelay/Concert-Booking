package booking.api.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 잔액 충전
     * @param userId 유저 PK
     * @param amount 잔액
     * @return 충전 후 유저 잔액 정보
     */
    @Transactional
    public User charge(Long userId, BigDecimal amount) {

        if(amount.equals(BigDecimal.ZERO)) throw new IllegalArgumentException("충전 금액이 0원입니다.");
        //유저 조회
        User user = userRepository.findLockByUserId(userId);
        user.chargeAmount(amount);
        return userRepository.saveUser(user);
    }

    /**
     * 잔액 조회
     * @param userId 유저 PK
     * @return 유저 잔액 정보
     */
    public User searchAmount(Long userId) {
        return userRepository.findByUserId(userId);
    }
}
