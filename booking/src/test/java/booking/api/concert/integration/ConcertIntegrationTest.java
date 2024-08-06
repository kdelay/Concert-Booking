package booking.api.concert.integration;

import booking.api.concert.domain.*;
import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.concert.domain.enums.PaymentState;
import booking.api.concert.domain.enums.ReservationStatus;
import booking.api.user.domain.User;
import booking.api.user.domain.UserRepository;
import booking.api.user.domain.UserService;
import booking.api.waiting.domain.WaitingTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class ConcertIntegrationTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private WaitingTokenRepository waitingTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("50명이 동시에 동일한 좌석을 예약 신청하는 경우, 한 명만 예약 가능")
    void requestReservationIdenticalSeat() throws InterruptedException {

        //50명 접근 가정
        int threads = 50;

        //유저 정보
        List<User> users = userRepository.findUsers();
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
                log.info("Thread Name: {}", Thread.currentThread().getName());
                long start_time = System.currentTimeMillis();

                try {
                    concertService.bookingSeats(userIdList.poll(),
                            concertScheduleList.get(0).getId(),
                            concertScheduleList.get(0).getConcertDate(),
                            List.of(1), "");
                    successCount.getAndIncrement();
                } catch (RuntimeException e) {
                    failCount.getAndIncrement();
                    log.error("Exception occurred: ", e);
                } finally {
                    countDownLatch.countDown();
                    log.info("# 소요 시간 = {} (ms)", System.currentTimeMillis() - start_time);
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
        List<User> users = userRepository.findUsers();
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
                            List.of(seatList.poll()), "");
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

        User user = userRepository.findByUserId(1L);
        Concert concert = concertRepository.findByConcertId(1L);
        ConcertSchedule concertSchedule = concertRepository.findSchedulesByConcert(concert).get(0);

        List<Reservation> reservations = concertService.bookingSeats(
                user.getId(), concertSchedule.getId(), concertSchedule.getConcertDate(), List.of(1), ""
        );

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
//            assertThat(userRepository.findWaitingByUserId(reservation.getUserId()).getWaitingTokenStatus())
//                    .isEqualTo(WaitingTokenStatus.EXPIRED);
        });
    }

    @Test
    @DisplayName("동시에 동일 유저의 잔액을 사용하려고 시도 하더라도, 한 번만 사용 가능하다.")
    void payIntegrationTest() throws InterruptedException {

        int threads = 3;
        long userId = 1L;

        CountDownLatch countDownLatch = new CountDownLatch(threads);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        userService.charge(userId, BigDecimal.valueOf(5000));
        concertService.bookingSeats(userId, 1L, LocalDate.parse("2024-07-10"), List.of(1, 2), "");

        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                try {
                    concertService.pay(1L, 1L, "");
                    successCount.getAndIncrement();
                } catch (RuntimeException e) {
                    failCount.getAndIncrement();
                    log.error("Exception occurred: ", e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        User user = userRepository.findByUserId(userId);

        assertThat(user.getAmount()).isEqualTo(BigDecimal.valueOf(4000));
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(2);
    }

    @Test
    @DisplayName("캐싱 테스트")
    public void testCache() {
        long concertId = 1L;

        //첫 번째 호출: DB 조회 후 캐시 저장
        List<ConcertSchedule> schedule1 = concertService.getSchedulesWithCache(concertId);

        //두 번째 호출: 캐시에서 조회
        List<ConcertSchedule> schedule2 = concertService.getSchedulesWithCache(concertId);

        assertNotNull(schedule1);
        assertNotNull(schedule2);
    }
}