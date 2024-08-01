package booking.api.waiting.domain;

import booking.support.exception.CustomBadRequestException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static booking.support.exception.ErrorCode.WAITING_TOKEN_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedissonClient redissonClient;

    @Value("${waitingToken.entryAmount:2000}")
    private int entryAmount;

    @Value("${waitingToken.processTime:10}")
    private long processTime;

    public String getNewToken() {

        long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String token = UUID.randomUUID().toString();

        if (getWaitingTokens().contains(token)) {
            throw new CustomBadRequestException(WAITING_TOKEN_ALREADY_EXISTS, "이미 존재하는 토큰입니다.");
        }

        getWaitingTokens().add(timestamp, token);
        return token;
    }

    public long getRank(String token) {
        return getWaitingTokens().rank(token);
    }

    public String getTtl(String token) {
        int expectedWaitingTime = getExpectedWaitingTime(token);
        System.out.println("expectedWaitingTime = " + expectedWaitingTime);
        return String.format("%d분 %02d초",
                expectedWaitingTime / 60,
                expectedWaitingTime % 60
        );
    }

    public void activateToken() {
        //참가열 추가 및 대기열 제거
        getWaitingTokens().valueRange(0, entryAmount - 1).forEach(token -> {
            redissonClient.getSetCache("activeTokens")
                    .add(token, 10, TimeUnit.MINUTES); //10분 뒤 토큰 만료
        });
        getWaitingTokens().removeRangeByRank(0, entryAmount - 1);
    }

    //------------------------------------------------------------------------------------------

    private RScoredSortedSet<String> getWaitingTokens() {
        return redissonClient.getScoredSortedSet("waitingTokens");
    }

    private int getExpectedWaitingTime(String token) {
        //(대기열 순번 / 초당 처리량) * 대기열 처리 시간
        return (int)((getRank(token) / entryAmount) * processTime);
    }
}
