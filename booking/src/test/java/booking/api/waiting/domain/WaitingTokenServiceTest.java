package booking.api.waiting.domain;

import booking.api.concert.domain.Concert;
import booking.api.concert.domain.ConcertRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WaitingTokenServiceTest {

    @InjectMocks
    WaitingTokenService waitingTokenService;

    @Mock
    WaitingTokenRepository waitingTokenRepository;

    @Mock
    ConcertRepository concertRepository;

    private List<User> userList;
    private List<WaitingToken> waitingTokenList;

    @BeforeEach
    void setUp() {
        //유저 기본 세팅
        userList = UserDummy.getUserList();
        for (User user : userList) {
            lenient().when(waitingTokenRepository.findByUserId(user.getId())).thenReturn(user);
        }

        //대기열 토큰 기본 세팅
        waitingTokenList = WaitingTokenDummy.getWaitingTokenList();
        for (WaitingToken waitingToken : waitingTokenList) {
            lenient().when(waitingTokenRepository.save(waitingToken)).thenReturn(waitingToken);
        }
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("헤더에 토큰이 없는 경우 신규 토큰 발급")
    void emptyHeaderThenIssueToken() {

        String token = null;
        Long userId = 1L;
        Long concertId = 1L;

        //유저
        userList.stream().filter(u -> u.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User - userId not found"));

        //콘서트
        Concert concert = Concert.create(concertId, "A 콘서트", "A");
        when(concertRepository.findByConcertId(concertId)).thenReturn(concert);

        //대기열 토큰 저장
        when(waitingTokenRepository.save(any(WaitingToken.class))).thenAnswer(invocation -> {
            WaitingToken savedToken = invocation.getArgument(0);
            return new WaitingToken(savedToken.getUser(), savedToken.getWaitingTokenStatus());
        });

        WaitingToken result = waitingTokenService.issue(token, userId, concertId);

        assertNotNull(result);
        assertNotNull(result.getToken());
    }

    @Test
    @DisplayName("헤더에 토큰이 있는 경우 현재 대기열 반환")
    void havingTokenInHeaderThenReturnWaiting() {

        Long userId = 1L;
        Long concertId = 1L;
        Long waitingTokenId = 1L;

        //유저
        User user = userList.stream().filter(u -> u.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User - userId not found"));

        //콘서트
        Concert concert = Concert.create(concertId, "A 콘서트", "A");

        //대기열 토큰
        WaitingToken waitingToken = waitingTokenList.stream().filter(w -> w.getId().equals(waitingTokenId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("WaitingToken - userId not found"));

        String token = waitingToken.getToken();

        //when
        when(waitingTokenRepository.findByUserId(userId)).thenReturn(user);
        when(concertRepository.findByConcertId(concertId)).thenReturn(concert);
        when(waitingTokenRepository.findUsingTokenByUserId(waitingTokenId)).thenReturn(waitingToken);

        WaitingToken result = waitingTokenService.issue(token, user.getId(), concert.getId());

        assertThat(result.getId()).isEqualTo(waitingTokenId);
        assertThat(result.getToken()).isEqualTo(token);
    }

    @Test
    @DisplayName("대기열 진입 여부에 따른 토큰 상태 테스트")
    void entryInWaiting() {

        //대기열 토큰 조회 + 대기열 진입 여부 확인
        for (WaitingToken waitingToken : waitingTokenList) search(waitingToken);

        //대기 중인 waitingToken 의 정보를 검증
        List<WaitingToken> deactivatedTokens = new ArrayList<>();
        for (WaitingToken waitingToken : waitingTokenList) {
            if (waitingToken.getWaitingTokenStatus() == WaitingTokenStatus.DEACTIVATE) {
                deactivatedTokens.add(waitingToken);
            }
        }

        //대기 중인 waitingToken 이 7개인지 확인
        assertThat(deactivatedTokens.size()).isEqualTo(7);

        //각 대기 중인 waitingToken 의 정보를 검증 (예: ID가 3 이상인지 확인)
        for (WaitingToken deactivatedToken : deactivatedTokens) {
            assertThat(deactivatedToken.getId()).isGreaterThanOrEqualTo(3L);
        }
    }

    private void search(WaitingToken waitingToken) {

        //순서
        when(waitingTokenRepository.findActivateTokenSortedByIdDesc()).thenReturn(1L);
        long rank = waitingTokenService.getRank(waitingToken.getId());

        //현재 시간(ms)
        long curDate = waitingToken.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        //5초당 3명씩 대기열에 들어갈 수 있다고 가정한다.
        long entryAmount = 3L;
        long processTime = 5000L;

        //입장 가능 시간 = 현재 시간(토큰 생성 시간) + (본인 순서 / 분당 처리량) * 대기열 처리 시간(사용자 지정)
        long entryAccessTime = curDate + (rank / entryAmount) * processTime;

        //대기열 입장 가능 시간이 된 경우 토큰 상태를 ACTIVATE 로 업데이트한다.
        if(curDate == entryAccessTime) waitingToken.updateTokenActivate();
    }
}