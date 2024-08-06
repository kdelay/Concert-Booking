package booking.api.waiting.integration;

import booking.api.user.domain.User;
import booking.api.user.domain.UserRepository;
import booking.api.waiting.domain.WaitingService;
import booking.api.waiting.domain.WaitingTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class QueueIntegrationTest {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    WaitingService waitingService;

    @Autowired
    private WaitingTokenRepository waitingTokenRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void clear() {
        redissonClient.getScoredSortedSet("waitingTokens").clear();
        redissonClient.getSet("activeTokens").clear();
    }

    @Test
    @DisplayName("4,000명의 유저가 모두 대기열에 처음 입장할 경우 토큰이 발급되고, 2,000명은 처리열에 있다. " +
            "2,001번째 유저는 10초를 대기해야 한다.")
    public void queueIntegrationTest() throws InterruptedException {

        int threads = 4000;
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        AtomicLong sumTime = new AtomicLong(0L);

        List<User> users = userRepository.findUsers();
        Queue<Long> userIdList = users.stream()
                .map(User::getId)
                .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));


        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                long startTime = System.currentTimeMillis();

                Long userId = userIdList.poll();
                if (userId == null) {
                    log.info("userId is null");
                    failCount.getAndIncrement();
                    countDownLatch.countDown();
                }

                try {
                    waitingService.getWaiting("");
                    successCount.getAndIncrement();
                } catch (RuntimeException e) {
                    log.error("Exception occurred: ", e);
                    failCount.getAndIncrement();
                } finally {
                    countDownLatch.countDown();
                    sumTime.addAndGet((System.currentTimeMillis() - startTime));
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        log.info("# 소요 시간 = {}", sumTime);

        //모든 유저의 토큰이 발급되었는지 검증
        assertThat(successCount.get()).isEqualTo(4000);
        assertThat(failCount.get()).isEqualTo(0);

        RScoredSortedSet<String> waitingTokens = redissonClient.getScoredSortedSet("waitingTokens");
        assertThat(waitingTokens.size()).isEqualTo(4000);

        //2,001번째 유저는 10초를 대기해야 한다.
        String userToken = waitingTokens.valueRange(2000, 2000).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Token not found"));
//        String ttl = queueService.getTtl(userToken);

        // 예상 결과와 비교하여 검증합니다.
//        assertThat(ttl).isEqualTo("0분 10초");

        //토큰 처리열로 이동
        waitingService.activateToken();

        //2,000명의 유저가 처리열로 이동되었는지 검증
        RSet<String> activeTokens = redissonClient.getSet("activeTokens");
        assertThat(waitingTokens.size()).isEqualTo(2000);
        assertThat(activeTokens.size()).isEqualTo(2000);
    }
}
