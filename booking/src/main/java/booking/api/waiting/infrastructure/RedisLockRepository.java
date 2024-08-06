package booking.api.waiting.infrastructure;

import booking.support.handler.LockHandler;
import booking.support.handler.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLockRepository implements LockHandler {

    private final RedissonClient redissonClient;
    private final TransactionHandler transactionHandler;

    @Override
    public Object runWithLock(
            String lockName, long waitTime, long leaseTime, TimeUnit timeUnit, Supplier<String> supplier
    ) throws InterruptedException {

        RLock lock = redissonClient.getLock(lockName);

        try {
            boolean available = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (!available) return false;
            return transactionHandler.runWithTransaction(supplier);
        } catch (InterruptedException e) {
            throw e;
        } finally {
            releaseLock(lock);
        }
    }

    @Override
    public void releaseLock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                throw new IllegalMonitorStateException("Redis Lock Already UnLock");
            }
        }
    }
}