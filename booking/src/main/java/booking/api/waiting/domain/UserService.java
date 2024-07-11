package booking.api.waiting.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final WaitingTokenRepository waitingTokenRepository;

    public User charge(Long userId, BigDecimal amount) {

        //유저 조회
        User user = waitingTokenRepository.findByUserId(userId);

        if(amount.equals(BigDecimal.ZERO)) throw new IllegalArgumentException("충전 금액이 0원입니다.");

        return user.chargeAmount(amount);
    }

    public User searchAmount(Long userId) {
        return waitingTokenRepository.findByUserId(userId);
    }
}
