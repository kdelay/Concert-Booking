package booking.api.concert.domain;

import booking.api.concert.domain.enums.ConcertSeatStatus;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @InjectMocks
    ConcertService concertService;

    @Mock
    ConcertRepository concertRepository;

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
    @DisplayName("콘서트 날짜 조회 - 대기열 검증에 실패한 경우")
    void failToAccessWaiting_schedules() {

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

    //-----------------------------------------------------------------------------------

    @Test
    @DisplayName("콘서트 좌석 조회 - 대기열 검증에 실패한 경우")
    void failToAccessWaiting_seats() {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");

        assertThatThrownBy(() -> concertService.searchSeats(null, concertScheduleId, concertDate))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("토큰 인증에 실패했습니다.");
    }

    @Test
    @DisplayName("콘서트 좌석 정보 조회 성공")
    void searchSeats() {

        long concertScheduleId = 1L;
        LocalDate concertDate = LocalDate.parse("2024-07-10");
        WaitingToken waitingToken = WaitingTokenDummy.getWaitingTokenList().get(0);
        ConcertSchedule concertSchedule = concertScheduleList.get(0);
        List<ConcertSeat> concertSeats = concertSeatList.stream()
                .filter(seat -> seat.getConcert().equals(concert))
                .filter(seat -> seat.getConcertSchedule().equals(concertSchedule))
                .filter(seat -> seat.getSeatStatus() == ConcertSeatStatus.AVAILABLE)
                .collect(Collectors.toList());

        when(concertRepository.findByScheduleIdAndConcertDate(concertScheduleId, concertDate)).thenReturn(concertScheduleList.get(0));
        when(concertRepository.findByConcertAndSchedule(concert, concertSchedule)).thenReturn(concertSeats);

        List<List<Object>> lists = concertService.searchSeats(waitingToken.getToken(), concertScheduleId, concertDate);
        assertThat(lists.size()).isEqualTo(50); //현재 예약 가능한 좌석은 50개
    }
}