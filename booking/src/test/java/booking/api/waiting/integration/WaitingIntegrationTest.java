package booking.api.waiting.integration;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.UserService;
import booking.api.waiting.domain.WaitingTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class WaitingIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private WaitingTokenRepository waitingTokenRepository;

    @BeforeEach
    void setUp() {
        long userId = 1L;
        User user = waitingTokenRepository.findByUserId(userId);
        user.resetAmount();
        waitingTokenRepository.saveUser(user);
    }

    @Test
    @DisplayName("동시에 동일 유저의 잔액을 중복 충전하려고 시도 하더라도, 한 번만 충전 가능하다.")
    void chargeAmountIntegrationTest() throws InterruptedException {

        int threads = 3;
        long userId = 1L;

        CountDownLatch countDownLatch = new CountDownLatch(threads);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                try {
                    User user = userService.charge(userId, BigDecimal.valueOf(1000));
                    log.info("amount = {}", user.getAmount());
                    successCount.getAndIncrement();
                } catch (RuntimeException e) {
                    failCount.getAndIncrement();
                    log.error("Exception occurred: ", e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        User user = waitingTokenRepository.findByUserId(userId);

        assertThat(user.getAmount()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(2);
    }
}
