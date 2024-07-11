package booking.api.concert.domain;

import booking.api.waiting.domain.WaitingToken;
import booking.api.waiting.domain.WaitingTokenDummy;
import booking.api.waiting.domain.WaitingTokenRepository;
import booking.common.exception.AuthorizationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @InjectMocks
    ConcertService concertService;

    @Mock
    ConcertRepository concertRepository;

    @Mock
    WaitingTokenRepository waitingTokenRepository;

    private Concert concert;
    private List<ConcertSchedule> concertScheduleList;
    private List<ConcertSeat> concertSeatList;

    @BeforeEach
    void setUp() {

        //콘서트 데이터 기본 세팅
        concert = Concert.create(1L, "A 콘서트", "A");
        lenient().when(concertRepository.findByConcertId(concert.getId())).thenReturn(concert);

        concertScheduleList = List.of(
            ConcertSchedule.create(1L, concert, LocalDate.parse("2024-07-10")),
            ConcertSchedule.create(2L, concert, LocalDate.parse("2024-07-11"))
        );
        lenient().when(concertRepository.findByConcertEntity(concert)).thenReturn(concertScheduleList);

        concertSeatList = ConcertSeatDummy.getConcertSeatList(concert, concertScheduleList);
        for (ConcertSeat seat : concertSeatList) {
            lenient().when(concertRepository.findBySeatId(seat.getId())).thenReturn(seat);
        }
    }

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("대기열 검증에 실패한 경우")
    void failToAccessWaiting() {

        Long concertId = 1L;

        assertThatThrownBy(() -> concertService.searchSchedules(null, concertId))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("토큰 인증에 실패했습니다.");
    }

    @Test
    @DisplayName("콘서트 날짜 정보 조회 성공")
    void searchSchedules() {

        Long concertId = 1L;
        WaitingToken waitingToken = WaitingTokenDummy.getWaitingTokenList().get(0);

        List<LocalDate> dates = concertService.searchSchedules(waitingToken.getToken(), concertId);
        List<Long> idList = concertService.getConcertScheduleId(concertId);

        assertThat(dates.get(0)).isEqualTo("2024-07-10");
        assertThat(dates.get(1)).isEqualTo("2024-07-11");
        assertThat(idList.get(0)).isEqualTo(1L);
        assertThat(idList.get(1)).isEqualTo(2L);
    }
}