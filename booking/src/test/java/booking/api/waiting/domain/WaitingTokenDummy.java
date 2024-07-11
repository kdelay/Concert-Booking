package booking.api.waiting.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static booking.api.waiting.domain.WaitingTokenStatus.*;

public class WaitingTokenDummy {

    public static List<WaitingToken> getWaitingTokenList() {
        long pk = 1L;
        String token = UUID.randomUUID().toString();

        List<WaitingToken> waitingTokenList = new ArrayList<>();
        List<User> userList = UserDummy.getUserList();

        for (User user : userList) {
            waitingTokenList.add(
                    WaitingToken.create(pk++, user, token, DEACTIVATE, LocalDateTime.now(), LocalDateTime.now())
            );
        }
        return waitingTokenList;
    }
}
