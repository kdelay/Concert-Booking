package booking.support.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class TransactionHandler {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T runWithTransaction(Supplier<T> supplier) {
        return supplier.get();
    }
}
