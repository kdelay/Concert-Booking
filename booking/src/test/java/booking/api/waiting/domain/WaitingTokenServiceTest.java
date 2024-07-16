package booking.api.waiting.domain;

import booking.api.concert.domain.Concert;
import booking.api.concert.domain.ConcertRepository;
import booking.common.exception.CustomNotFoundException;
import booking.dummy.UserDummy;
import booking.dummy.WaitingTokenDummy;
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

import static booking.api.waiting.domain.WaitingTokenStatus.ACTIVATE;
import static booking.api.waiting.domain.WaitingTokenStatus.DEACTIVATE;
import static booking.common.exception.ErrorCode.USER_IS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
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
    private Concert concert;
    private List<WaitingToken> waitingTokenList;

    @BeforeEach
    void setUp() {
        //유저 기본 세팅
        userList = UserDummy.getUserList();
        for (User user : userList) {
            lenient().when(waitingTokenRepository.findByUserId(user.getId())).thenReturn(user);
        }

        //콘서트 기본 세팅
        concert = Concert.create(1L, "A 콘서트", "A");
        lenient().when(concertRepository.findByConcertId(1L)).thenReturn(concert);

        //대기열 토큰 기본 세팅
        waitingTokenList = WaitingTokenDummy.getWaitingTokenList();
        for (WaitingToken waitingToken : waitingTokenList) {
            lenient().when(waitingTokenRepository.save(waitingToken)).thenReturn(waitingToken);
        }
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("유저 정보가 없는 경우")
    void emptyUser() {

        //given
        long userId = 1L;
        long concertId = 1L;

        //유저 정보 조회
        when(waitingTokenRepository.findByUserId(userId))
                .thenThrow(new CustomNotFoundException(USER_IS_NOT_FOUND, "해당하는 유저가 없습니다. [userId : %d]".formatted(userId)));

        //콘서트 정보 조회
        lenient().when(concertRepository.findByConcertId(concertId)).thenReturn(concert);

        //when & then
        assertThatThrownBy(() -> waitingTokenService.issueTokenOrSearchWaiting(anyString(), userId, concertId))
                .isInstanceOf(CustomNotFoundException.class)
                .hasMessage("[USER_IS_NOT_FOUND] 해당하는 유저가 없습니다. [userId : %d]".formatted(userId));
    }

    @Test
    @DisplayName("헤더에 토큰이 없거나 대기열 토큰 정보가 없는 경우 신규 토큰 발급")
    void emptyHeaderThenIssueToken() {

        //given
        String token = null;
        long userId = 1L;
        long concertId = 1L;

        //만료되지 않은 대기열 토큰 정보 조회
        when(waitingTokenRepository.findUsingTokenByUserId(userId)).thenReturn(null);

        //콘서트 정보 조회
        lenient().when(concertRepository.findByConcertId(concertId)).thenReturn(concert);

        //대기열 순서 번호
        when(waitingTokenRepository.findActivateTokenSortedByIdDesc()).thenReturn(123L);

        //대기열 토큰 저장
        when(waitingTokenRepository.save(any(WaitingToken.class))).thenAnswer(invocation -> {
            WaitingToken savedToken = invocation.getArgument(0);
            return new WaitingToken(1L, savedToken.getUser(), savedToken.getToken(),
                    savedToken.getWaitingTokenStatus(), savedToken.getCreatedAt(), savedToken.getModifiedAt());
        });

        //when
        WaitingToken result = waitingTokenService.issueTokenOrSearchWaiting(token, userId, concertId);

        //토큰이 있는지 검증
        assertNotNull(result);
        assertNotNull(result.getToken());
    }

    @Test
    @DisplayName("헤더에 토큰이 있고 대기열 토큰 정보가 있는 경우 유저의 현재 대기열 반환")
    void havingTokenInHeaderThenReturnWaiting() {

        //given
        long userId = 1L;
        long waitingTokenId = 1L;
        long concertId = 1L;

        //유저 정보 조회
        User user = userList.stream().filter(u -> u.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new CustomNotFoundException(USER_IS_NOT_FOUND, "해당하는 유저가 없습니다. [userId : %d]".formatted(userId)));
        when(waitingTokenRepository.findByUserId(userId)).thenReturn(user);

        //콘서트 정보 조회
        lenient().when(concertRepository.findByConcertId(concertId)).thenReturn(concert);

        //대기열 토큰 조회
        WaitingToken waitingToken = waitingTokenList.stream().filter(w -> w.getId().equals(waitingTokenId)).findFirst().orElse(null);
        when(waitingTokenRepository.findUsingTokenByUserId(waitingTokenId)).thenReturn(waitingToken);

        //토큰 정보
        assert waitingToken != null;
        String token = waitingToken.getToken();

        //when
        WaitingToken result = waitingTokenService.issueTokenOrSearchWaiting(token, user.getId(), anyLong());

        //토큰 정보와 id 가 동일한지 검증
        assertThat(result.getId()).isEqualTo(waitingTokenId);
        assertThat(result.getToken()).isEqualTo(token);
    }

    @Test
    @DisplayName("대기열 진입 여부에 따른 토큰 상태 테스트")
    void tokenStateTest() {

        //대기열 토큰 조회 + 대기열 진입 여부 확인
        for (WaitingToken waitingToken : waitingTokenList) checkEntryAndUpdateState(waitingToken);

        //대기 중인 waitingToken 의 정보를 검증
        List<WaitingToken> deactivatedTokens = new ArrayList<>();
        for (WaitingToken waitingToken : waitingTokenList) {
            if (waitingToken.getWaitingTokenStatus() == DEACTIVATE) {
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

    private void checkEntryAndUpdateState(WaitingToken waitingToken) {

        //대기열 순서 번호
        when(waitingTokenRepository.findActivateTokenSortedByIdDesc()).thenReturn(1L);
        long rank = waitingTokenService.getRank(waitingToken.getId());

        //토큰 생성 시간(ms)
        long tokenCreatedAt = waitingToken.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        //5초당 3명씩 대기열에 들어갈 수 있다고 가정한다.
        long entryAmount = 3L;
        long processTime = 5000L;

        //대기열 입장 가능 시간 = 토큰 생성 시간 + ((대기열 순서 번호 / 분당 처리량) * 대기열 처리 시간)
        long entryAccessTime = tokenCreatedAt + ((rank / entryAmount) * processTime);

        //대기열 입장 가능 시간이 된 경우 토큰 상태를 ACTIVATE 로 업데이트한다.
        if(tokenCreatedAt == entryAccessTime) waitingToken.updateWaitingTokenStatus(ACTIVATE);
    }
}