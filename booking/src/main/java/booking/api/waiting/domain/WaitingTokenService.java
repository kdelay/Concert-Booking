package booking.api.waiting.domain;

import booking.api.concert.domain.ConcertRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

import static booking.api.waiting.domain.WaitingTokenStatus.DEACTIVATE;

@Service
@RequiredArgsConstructor
@Transactional
public class WaitingTokenService {

    private final WaitingTokenRepository waitingTokenRepository;
    private final ConcertRepository concertRepository;

    //대기열 토큰 발급
    public WaitingToken issue(String token, Long userId, Long concertId) {

        //유저 조회
        User user = waitingTokenRepository.findByUserId(userId);

        //콘서트 조회
        concertRepository.findByConcertId(concertId);

        //토큰이 없는 경우 토큰을 발급하고 대기열 정보를 반환한다.
        if (StringUtils.isEmpty(token)) {
            WaitingToken newToken = new WaitingToken(user, DEACTIVATE);
            return waitingTokenRepository.save(newToken);
        }
        //대기열 정보를 조회해서 반환한다.
        return waitingTokenRepository.findUsingTokenByUserId(userId);
    }

    //대기열 토큰 조회
    public WaitingToken search(Long userId) {

        WaitingToken waitingToken = waitingTokenRepository.findUsingTokenByUserId(userId);

        //순서
        long rank = getRank(waitingToken.getId());

        //현재 시간(ms)
        long curDate = waitingToken.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        //5초당 3명씩 대기열에 들어갈 수 있다고 가정한다.
        long entryAmount = 3L;
        long processTime = 5000L;

        //입장 가능 시간 = 현재 시간(토큰 생성 시간) + (본인 순서 / 분당 처리량) * 대기열 처리 시간(사용자 지정)
        long entryAccessTime = curDate + (rank / entryAmount) * processTime;

        //대기열 입장 가능 시간이 된 경우 토큰 상태를 ACTIVATE 로 업데이트한다.
        if(curDate == entryAccessTime) waitingToken.updateTokenActivate();
        return waitingTokenRepository.save(waitingToken);
    }

    public long getRank(Long waitingTokenId) {;
        Long rate = waitingTokenRepository.findActivateTokenSortedByIdDesc();
        return waitingTokenId - rate;
    }
}
