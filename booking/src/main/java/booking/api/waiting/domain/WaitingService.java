package booking.api.waiting.domain;

import booking.api.waiting.domain.enums.WaitingTokenStatus;
import booking.support.exception.CustomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static booking.support.exception.ErrorCode.WAITING_TOKEN_IS_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingTokenRepository waitingTokenRepository;

    @Value("${waitingToken.entryAmount:2000}")
    private int entryAmount;

    @Value("${waitingToken.processTime:10}")
    private long processTime;

    /**
     * 대기열 토큰 등록/조회
     * @param reqToken 헤더 토큰 정보(Authorization)
     * @return 대기열 정보
     */
    public WaitingToken getWaiting(String reqToken) {

        String token = null;

        //신규 토큰 발급
        if (reqToken == null || !waitingTokenRepository.findWaitingQueue(reqToken.substring(7))) {
            token = waitingTokenRepository.addWaitingQueue();
            return this.getWaitingInfo(token);
        }
        //기존 대기열 정보 반환
        return this.getWaitingInfo(token);
    }

    /**
     * @param token 토큰 값
     * @return 대기열 정보
     */
    private WaitingToken getWaitingInfo(String token) {

        //((대기열 순번 / 초당 처리량) * 대기열 처리 시간)(ms)
        Long rank = waitingTokenRepository.findWaitingRank(token);
        long waitingTime = (rank / entryAmount) * processTime;

        LocalDateTime remainingTime = LocalDateTime.now().plusSeconds(waitingTime);

        return WaitingToken.builder()
                .token(token)
                .rank(rank)
                .waitingTokenStatus(WaitingTokenStatus.WAITING)
                .entryAt(remainingTime)
                .build();
    }

    /**
     * 대기 토큰 -> 활성 토큰
     */
    public void activateToken() {
        if (waitingTokenRepository.isExistWaiting()) {
            List<String> tokens = waitingTokenRepository.popWaitingList(entryAmount);
            waitingTokenRepository.activeTokens(tokens);
        }
    }

    /**
     * 해당 토큰이 처리열에 있는지 확인
     * @param token 토큰
     */
    public void checkActiveQueue(String token) {
        if (Boolean.FALSE.equals(waitingTokenRepository.findActiveQueue(token)))
            throw new CustomNotFoundException(WAITING_TOKEN_IS_NOT_FOUND, "토큰이 활성 상태가 아닙니다.");
    }
}
