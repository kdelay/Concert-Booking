package booking.api.waiting.domain;

import booking.dummy.UserDummy;
import booking.dummy.WaitingTokenDummy;
import booking.support.exception.CustomNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static booking.api.waiting.domain.WaitingTokenStatus.ACTIVATE;
import static booking.api.waiting.domain.WaitingTokenStatus.DEACTIVATE;
import static booking.support.exception.ErrorCode.USER_IS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        //value mocking
        ReflectionTestUtils.setField(waitingTokenService, "entryAmount", 3);
        ReflectionTestUtils.setField(waitingTokenService, "processTime", 60000);
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("유저 정보가 없는 경우")
    void emptyUser() {

        //given
        long userId = 1L;

        //유저 정보 조회
        when(waitingTokenRepository.findByUserId(userId))
                .thenThrow(new CustomNotFoundException(USER_IS_NOT_FOUND, "해당하는 유저가 없습니다. [userId : %d]".formatted(userId)));

        //when & then
        assertThatThrownBy(() -> waitingTokenService.issueToken(userId))
                .isInstanceOf(CustomNotFoundException.class)
                .hasMessage("[USER_IS_NOT_FOUND] 해당하는 유저가 없습니다. [userId : %d]".formatted(userId));
    }

    @Test
    @DisplayName("비활성화 상태의 대기열 토큰 정보가 없는 경우")
    void emptyWaitingToken() {

        when(waitingTokenRepository.findDeactivateTokens()).thenReturn(List.of());

        List<WaitingToken> list = waitingTokenService.getDeactivateTokens();
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("대기열 토큰 정보가 없는 경우 신규 토큰 발급")
    void emptyHeaderThenIssueToken() {

        //유저 정보
        User user = userList.get(0);
        Long userId = user.getId();

        //만료되지 않은 대기열 토큰 정보가 없는 경우
        when(waitingTokenRepository.findNotExpiredToken(userId)).thenReturn(null);

        //신규 토큰 발급
        WaitingToken waitingToken = waitingTokenList.get(0);
        when(waitingTokenRepository.save(any(WaitingToken.class))).thenReturn(waitingToken);

        WaitingToken result = waitingTokenService.issueToken(userId);

        //토큰이 있는지 검증
        assertNotNull(result);
        assertNotNull(result.getToken());
    }

    @Test
    @DisplayName("대기열 토큰 정보가 있는 경우 유저의 현재 대기열 반환")
    void havingTokenInHeaderReturnWaiting() {

        //유저 정보
        User user = userList.get(0);
        Long userId = user.getId();

        //만료되지 않은 대기열 토큰 정보가 있는 경우
        WaitingToken waitingToken = waitingTokenList.get(0);
        when(waitingTokenRepository.findNotExpiredToken(userId)).thenReturn(waitingToken);

        WaitingToken result = waitingTokenService.issueToken(userId);

        //유저가 가지고 있는 대기열 토큰인지 검증
        assertThat(result.getUser().getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("대기열 진입 여부에 따른 토큰 상태 테스트")
    void tokenStateTest() {

        List<WaitingToken> waitingTokens = userList.stream()
                .map(user -> new WaitingToken(
                        user.getId(), 0L, user, "valid-token", DEACTIVATE,
                        LocalDateTime.now().minusMinutes(user.getId()), null))
                .toList();

        //순서
        Long lastActivateId = waitingTokens.stream()
                .filter(waitingToken -> ACTIVATE.equals(waitingToken.getWaitingTokenStatus()))
                .map(WaitingToken::getId)
                .max(Comparator.naturalOrder())
                .orElse(0L);
        when(waitingTokenRepository.findLastActivateWaitingId()).thenReturn(lastActivateId);

        //대기열 입장 시간 확인
        waitingTokens.forEach(waitingTokenService::activateTokens);

        //대기 중인 waitingToken 의 정보를 검증
        List<WaitingToken> deactivateTokens = waitingTokens.stream()
                .filter(waitingToken -> waitingToken.getWaitingTokenStatus() == DEACTIVATE)
                .toList();

        //대기 중인 waitingToken 이 8개인지 확인
        assertThat(deactivateTokens.size()).isEqualTo(8);

        //각 대기 중인 waitingToken 의 정보를 검증 (예: ID가 3 이상인지 확인)
        assertThat(deactivateTokens.stream().allMatch(deactivateToken -> deactivateToken.getId() >= 3L)).isTrue();
    }

    @Test
    @DisplayName("현재 대기열 순서 번호 테스트")
    void getRankTest() {

        when(waitingTokenRepository.findLastActivateWaitingId()).thenReturn(0L);

        long rank = waitingTokenService.getRank(1L);
        assertThat(rank).isEqualTo(1L);
    }
}