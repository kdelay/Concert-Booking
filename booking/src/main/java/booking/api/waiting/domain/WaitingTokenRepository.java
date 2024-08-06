package booking.api.waiting.domain;

import java.util.List;

public interface WaitingTokenRepository {

    //redis
    String addWaitingQueue();

    Boolean findActiveQueue(String token);

    Boolean findWaitingQueue(String token);

    boolean isExistWaiting();

    Long findWaitingRank(String token);

    List<String> popWaitingList(int entryAmount);

    void activeTokens(List<String> tokens);

    void setTtl(String token);

    void expireToken(String token);
}
