package booking.dummy;

import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingToken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static booking.api.waiting.domain.WaitingTokenStatus.DEACTIVATE;

public class WaitingTokenDummy {

    public static List<WaitingToken> getWaitingTokenList() {
        long pk = 1L;

        List<WaitingToken> waitingTokenList = new ArrayList<>();
        List<User> userList = UserDummy.getUserList();

        for (User user : userList) {
            waitingTokenList.add(
                    new WaitingToken(pk++, 0L, user, UUID.randomUUID().toString(), DEACTIVATE, LocalDateTime.now(), null)
            );
        }
        return waitingTokenList;
    }
}
