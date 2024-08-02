package booking.api.waiting.domain;

import booking.support.exception.CustomBadRequestException;
import booking.support.exception.CustomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSetMultimap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static booking.support.exception.ErrorCode.WAITING_TOKEN_ALREADY_EXISTS;
import static booking.support.exception.ErrorCode.WAITING_TOKEN_IS_NOT_FOUND;

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
        Integer rank = getWaitingTokens().rank(token);
        if (rank == null) throw new CustomNotFoundException(WAITING_TOKEN_IS_NOT_FOUND, "이미 입장하거나 없는 토큰입니다.");
        return rank;
    }

    public String getTtl(String token) {
        int expectedWaitingTime = getExpectedWaitingTime(token);
        return String.format("%d분 %02d초",
                expectedWaitingTime / 60,
                expectedWaitingTime % 60
        );
    }

    public void activateToken() {

        RSetMultimap<String, Long> activeTokens = redissonClient.getSetMultimap("activeTokens");

        //10분 후 만료 ttl 설정
        long expiredAt = LocalDateTime.now().plusMinutes(10)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        //참가열 추가 및 대기열 제거
        getWaitingTokens().valueRange(0, entryAmount - 1)
                .forEach(token -> activeTokens.put(token, expiredAt));

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
