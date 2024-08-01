package booking.support;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface LockHandler {

    Object runWithLock(String lockName, long waitTime, long leaseTime, TimeUnit timeUnit, Supplier<String> supplier) throws InterruptedException;
    void releaseLock(RLock lock);
}
