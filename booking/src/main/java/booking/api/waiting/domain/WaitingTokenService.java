package booking.api.waiting.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitingTokenService {

    private final WaitingTokenRepository waitingTokenRepository;

    @Value("${waitingToken.entryAmount:3}")
    private int entryAmount;

    @Value("${waitingToken.processTime:60000}")
    private long processTime;

    /**
     * 대기열 토큰 발급
     * @param userId 유저 PK
     * @return 대기열 정보
     */
    public WaitingToken issueToken(Long userId) {

        //유저 정보 조회
        User user = waitingTokenRepository.findByUserId(userId);

        //만료되지 않은 대기열 토큰 정보 조회
        WaitingToken waitingToken = waitingTokenRepository.findNotExpiredToken(user.getId());

        //대기열 토큰 정보가 없는 경우 새로운 토큰을 발급한다.
        if (waitingToken == null) {
            waitingToken = waitingTokenRepository.save(WaitingToken.create(user));
        }
        return waitingToken;
    }

    /**
     * 대기열 입장 가능 시간 확인
     * 토큰 상태 ACTIVATE 로 변경
     * @param waitingToken 대기열 정보
     */
    @Transactional
    public void activateTokens(WaitingToken waitingToken) {
        //대기열 순서 번호
        long rank = getRank(waitingToken.getId());

        //대기열 입장 가능 시간이 된 경우, 토큰 상태를 ACTIVATE 로 업데이트한다.
        if(waitingToken.isEntryTime(rank, entryAmount, processTime)) {
            waitingToken.activateToken();
            waitingToken.updateTime();
            waitingTokenRepository.save(waitingToken);
        }
    }

    /**
     * @param waitingTokenId 대기열 PK
     * @return 현재 대기열 순서 번호
     */
    public long getRank(Long waitingTokenId) {
        //대기열 순서 번호 = (현재 유저 id 번호 - 가장 마지막에 활성화 된 유저의 id 번호)
        long lastActivateId = waitingTokenRepository.findLastActivateWaitingId();
        return waitingTokenId - lastActivateId;
    }

    /**
     * @return 비활성화 상태의 모든 대기열 정보 조회
     */
    public List<WaitingToken> getDeactivateTokens() {
        return waitingTokenRepository.findDeactivateTokens();
    }


}
