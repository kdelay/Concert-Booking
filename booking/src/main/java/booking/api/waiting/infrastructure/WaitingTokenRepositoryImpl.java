package booking.api.waiting.infrastructure;

import booking.api.waiting.domain.WaitingTokenRepository;
import booking.support.exception.CustomNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static booking.support.exception.ErrorCode.WAITING_TOKEN_IS_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class WaitingTokenRepositoryImpl implements WaitingTokenRepository {

    private final static String ACTIVE_KEY_PREFIX = "ACTIVE:";
    public static final String WAITING_KEY_PREFIX = "WAITING";

    private final StringRedisTemplate redisTemplate;
    private ZSetOperations<String, String> zSetOperations;
    private ValueOperations<String, String> valueOperations;

    @PostConstruct
    public void init() {
        zSetOperations = redisTemplate.opsForZSet();
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void activeTokens(List<String> tokens) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            tokens.forEach(token -> {
                String key = ACTIVE_KEY_PREFIX + token;
                connection.stringCommands().set(key.getBytes(), token.getBytes());
                connection.commands().expire(key.getBytes(), 600);
            });
            return null;
        });
    }

    @Override
    public void setTtl(String token) {
        String value = token.substring(7);
        String key = ACTIVE_KEY_PREFIX + value;
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
            stringRedisConnection.setEx(key, 300, value);
            return null;
        });
    }

    @Override
    public void expireToken(String token) {
        String key = ACTIVE_KEY_PREFIX + token.substring(7);
        valueOperations.getAndDelete(key);
    }

    @Override
    public Boolean findActiveQueue(String token) {
        String value = valueOperations.get(ACTIVE_KEY_PREFIX + token);
        if (value == null) throw new CustomNotFoundException(WAITING_TOKEN_IS_NOT_FOUND, "토큰이 활성 상태가 아닙니다.");
        return !Objects.requireNonNull(value).isEmpty();
    }

    @Override
    public Boolean findWaitingQueue(String token) {
        Long rank = zSetOperations.rank(WAITING_KEY_PREFIX, token);
        return rank != null;
    }

    @Override
    public boolean isExistWaiting() {
        Long size = zSetOperations.size(WAITING_KEY_PREFIX);
        return size != null && size > 0;
    }

    @Override
    public String addWaitingQueue() {
        String token = UUID.randomUUID().toString();
        long score = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        zSetOperations.add(WAITING_KEY_PREFIX, token, score);
        return token;
    }

    @Override
    public Long findWaitingRank(String token) {
        Long rank = zSetOperations.rank(WAITING_KEY_PREFIX, token);
        if (rank == null) throw new CustomNotFoundException(WAITING_TOKEN_IS_NOT_FOUND, "이미 입장하거나 없는 토큰입니다.");
        return rank;
    }

    @Override
    public List<String> popWaitingList(int range) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.popMin(WAITING_KEY_PREFIX, range);
        return Optional.ofNullable(typedTuples)
                .map(set -> set.stream()
                        .map(ZSetOperations.TypedTuple::getValue)
                        .toList())
                .orElse(null);
    }
}
