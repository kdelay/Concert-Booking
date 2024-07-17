package booking.api.waiting.domain;

import booking.api.concert.domain.ConcertRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static booking.api.waiting.domain.WaitingTokenStatus.ACTIVATE;

@Service
@RequiredArgsConstructor
public class WaitingTokenService {

    private final WaitingTokenRepository waitingTokenRepository;
    private final ConcertRepository concertRepository;

    /**
     * 대기열 토큰 발급 및 조회
     * @param token Auth - Bearer Token
     * @param userId 유저 PK
     * @param concertId 콘서트 PK
     * @return 대기열 정보
     */
    @Transactional(rollbackFor = {Exception.class})
    public WaitingToken issueTokenOrSearchWaiting(String token, Long userId, Long concertId) {

        //유저 정보 조회
        User user = waitingTokenRepository.findByUserId(userId);

        //콘서트 정보 조회
        concertRepository.findByConcertId(concertId);

        //만료되지 않은 대기열 토큰 정보 조회
        WaitingToken waitingToken = waitingTokenRepository.findUsingTokenByUserId(userId);

        //대기열 토큰 정보가 없는 경우 새로운 토큰을 발급한다.
        if (waitingToken == null || token == null || StringUtils.isEmpty(token)) {
            waitingToken = waitingTokenRepository.save(WaitingToken.create(user));
        }

        checkEntryAndUpdateState(waitingToken);
        return waitingTokenRepository.save(waitingToken);
    }

    /**
     * 대기열 입장 가능 시간 확인 및 입장 시 토큰 상태 변경
     * @param waitingToken 대기열 토큰 정보
     */
    private void checkEntryAndUpdateState(WaitingToken waitingToken) {
        //대기열 순서 번호
        long rank = getRank(waitingToken.getId());

        //토큰 생성 시간(ms)
        long tokenCreatedAt = WaitingToken.getCreatedAtToMilli(waitingToken.getCreatedAt());

        //5초당 3명씩 대기열에 들어갈 수 있다고 가정한다.
        long entryAmount = 3L;
        long processTime = 5000L;

        //대기열 입장 가능 시간 = 토큰 생성 시간 + ((대기열 순서 번호 / 분당 처리량) * 대기열 처리 시간)
        long entryAccessTime = tokenCreatedAt + ((rank / entryAmount) * processTime);

        //대기열 입장 가능 시간이 된 경우, 토큰 상태를 ACTIVATE 로 업데이트한다.
        if(tokenCreatedAt == entryAccessTime) waitingToken.updateWaitingTokenStatus(ACTIVATE);
    }

    /**
     * 토큰을 가지고 있는 유저의 현재 대기열 순서 번호 반환
     * @param waitingTokenId 대기열 토큰 PK
     * @return 대기열 순서 번호
     */
    public long getRank(Long waitingTokenId) {
        //대기열 순서 번호 = (현재 유저 id 번호 - 가장 마지막에 활성화 된 유저의 id 번호)
        Long lastActivateId = waitingTokenRepository.findActivateTokenSortedByIdDesc();
        return waitingTokenId - lastActivateId;
    }
}
