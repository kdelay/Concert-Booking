package booking.api.concert.integration;

import booking.api.concert.domain.*;
import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.concert.domain.enums.PaymentState;
import booking.api.concert.domain.enums.ReservationStatus;
import booking.api.waiting.domain.User;
import booking.api.waiting.domain.WaitingTokenRepository;
import booking.api.waiting.domain.WaitingTokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ConcertIntegrationTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private WaitingTokenRepository waitingTokenRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Test
    @DisplayName("50명이 동시에 동일한 좌석을 예약 신청하는 경우, 한 명만 예약 가능")
    void requestReservationIdenticalSeat() throws InterruptedException {

        //50명 접근 가정
        int threads = 50;

        //유저 정보
        List<User> users = waitingTokenRepository.findUsers();
        Queue<Long> userIdList = users.stream()
                .map(User::getId)
                .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));

        //콘서트 날짜 정보
        Concert concert = concertRepository.findByConcertId(1L);
        List<ConcertSchedule> concertScheduleList = concertRepository.findSchedulesByConcert(concert);

        //50개로 초기화한다. 카운트가 0이 되면 스레드를 대기상태에서 해제한다.
        CountDownLatch countDownLatch = new CountDownLatch(threads);

        //각 스레드를 생성해서 작업을 처리하고, 처리가 완료되면 해당 스레드를 제거한다. -> Task 를 실행하고 관리한다. (Queue 로 관리됨)
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        //멀티 스레드 환경에서 동시성을 보장해주는 변수
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                try {
                    concertService.bookingSeats(userIdList.poll(),
                            concertScheduleList.get(0).getId(),
                            concertScheduleList.get(0).getConcertDate(),
                            List.of(1));
                    successCount.getAndIncrement();
                } catch (RuntimeException e) {
                    failCount.getAndIncrement();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        List<Reservation> reservations = concertRepository.findAllByReservationStatus(ReservationStatus.RESERVING);

        //성공한 예약자 1명 검증
        assertThat(reservations).hasSize(1);
        assertThat(successCount.get()).isEqualTo(1);
        //그 외 실패자 49명 검증
        assertThat(failCount.get()).isEqualTo(49);
    }

    @Test
    @DisplayName("10명이 동시에 서로 다른 좌석을 예약하려는 경우, 모두 성공")
    void requestReservationAnotherSeats() throws InterruptedException {

        //10명 접근 가정
        int threads = 10;

        //유저 정보
        List<User> users = waitingTokenRepository.findUsers();
        Queue<Long> userIdList = users.stream()
                .map(User::getId)
                .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));

        //콘서트 날짜 정보
        Concert concert = concertRepository.findByConcertId(1L);
        List<ConcertSchedule> concertScheduleList = concertRepository.findSchedulesByConcert(concert);

        //좌석 정보
        List<ConcertSeat> seats = concertRepository.findSeats(concert, concertScheduleList.get(0));
        Queue<Integer> seatList = seats.stream()
                .map(ConcertSeat::getSeatNumber)
                .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));

        //10개로 초기화한다. 카운트가 0이 되면 스레드를 대기상태에서 해제한다.
        CountDownLatch countDownLatch = new CountDownLatch(threads);

        //각 스레드를 생성해서 작업을 처리하고, 처리가 완료되면 해당 스레드를 제거한다. -> Task 를 실행하고 관리한다. (Queue 로 관리됨)
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        //멀티 스레드 환경에서 동시성을 보장해주는 변수
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                try {
                    concertService.bookingSeats(userIdList.poll(),
                            concertScheduleList.get(0).getId(),
                            concertScheduleList.get(0).getConcertDate(),
                            List.of(seatList.poll()));
                    successCount.getAndIncrement();
                } catch (RuntimeException e) {
                    failCount.getAndIncrement();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        List<Reservation> reservations = concertRepository.findAllByReservationStatus(ReservationStatus.RESERVING);

        //성공한 예약자 10명 검증
        assertThat(reservations).hasSize(10);
        assertThat(successCount.get()).isEqualTo(10);
        //그 외 실패자 0명 검증
        assertThat(failCount.get()).isEqualTo(0);
    }

    @Test
    @DisplayName("예약 후 1분 이내에 결제 상태가 변경되지 않으면 예약, 결제가 취소되고 좌석 상태를 AVAILABLE 로 변경하고 대기열 토큰을 만료시킨다.")
    void noPayedInTimeThenCanceledAndExpiredToken() throws InterruptedException {

        User user = waitingTokenRepository.findByUserId(1L);
        Concert concert = concertRepository.findByConcertId(1L);
        ConcertSchedule concertSchedule = concertRepository.findSchedulesByConcert(concert).get(0);

        List<Reservation> reservations = concertService.bookingSeats(user.getId(), concertSchedule.getId(), concertSchedule.getConcertDate(), List.of(1));

        //2분 전 데이터로 세팅
        reservations.forEach(reservation -> {
            reservation.twoMinutesAgo();
            concertRepository.saveReservation(reservation);
        });

        //1분 이내에 결제가 완료되지 않은 경우
        concertService.expiredToken();

        reservations.forEach(reservation -> {

            //예약 상태 검증
            assertThat(concertRepository.findByReservationId(reservation.getId()).getReservationStatus())
                    .isEqualTo(ReservationStatus.CANCELED);

            //결제 상태 검증
            assertThat(concertRepository.findPaymentByReservation(reservation.getId()).getPaymentState())
                    .isEqualTo(PaymentState.CANCELED);

            //좌석 상태 검증
            assertThat(concertRepository.findBySeatId(reservation.getConcertSeatId()).getSeatStatus())
                    .isEqualTo(ConcertSeatStatus.AVAILABLE);

            //대기열 상태 검증
            assertThat(waitingTokenRepository.findWaitingByUserId(reservation.getUserId()).getWaitingTokenStatus())
                    .isEqualTo(WaitingTokenStatus.EXPIRED);
        });
    }
}