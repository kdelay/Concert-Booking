package booking.api.concert.integration;

import booking.api.concert.application.ConcertFacade;
import booking.api.concert.domain.*;
import booking.api.concert.domain.enums.ConcertSeatStatus;
import booking.api.concert.domain.enums.PaymentState;
import booking.api.concert.domain.enums.ReservationStatus;
import booking.api.waiting.application.WaitingTokenFacade;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ConcertIntegrationTest {

    @Autowired
    ConcertFacade concertFacade;

    @Autowired
    WaitingTokenFacade waitingTokenFacade;

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    WaitingTokenRepository waitingTokenRepository;

    @Test
    @DisplayName("50명이 동시에 동일한 좌석을 예약 신청하는 경우, 한 명만 예약 가능")
    void requestReservationIdenticalSeat() throws InterruptedException {

        //50명 접근 가정
        int threads = 50;

        //유저 정보
        Queue<Long> userIdList = new ConcurrentLinkedDeque<>();
        List<User> users = waitingTokenRepository.findUsers();
        for (User user : users) {
            userIdList.add(user.getId());
        }

        //콘서트 날짜 정보
        Concert concert = concertRepository.findByConcertId(1L);
        List<ConcertSchedule> concertScheduleList = concertRepository.findByConcertEntity(concert);

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
                    concertFacade.bookingSeats(userIdList.poll(),
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
        Queue<Long> userIdList = new ConcurrentLinkedDeque<>();
        List<User> users = waitingTokenRepository.findUsers();
        for (User user : users) {
            userIdList.add(user.getId());
        }

        //콘서트 날짜 정보
        Concert concert = concertRepository.findByConcertId(1L);
        List<ConcertSchedule> concertScheduleList = concertRepository.findByConcertEntity(concert);

        //좌석 정보
        Queue<Integer> seatList = new ConcurrentLinkedDeque<>();
        List<ConcertSeat> seats = concertRepository.findByConcertAndSchedule(concert, concertScheduleList.get(0));
        for (ConcertSeat seat : seats) {
            seatList.add(seat.getSeatNumber());
        }

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
                    concertFacade.bookingSeats(userIdList.poll(),
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
        ConcertSchedule concertSchedule = concertRepository.findByConcertEntity(concert).get(0);

        //좌석 예약 요청
        List<Reservation> reservations = concertFacade.bookingSeats(user.getId(), concertSchedule.getId(), concertSchedule.getConcertDate(), List.of(1));

        //1분 이내에 결제가 완료되지 않은 경우
        concertFacade.checkExpiredTimeForSeat();

        //1분 대기
        Thread.sleep(60000);

        for (Reservation reservation : reservations) {
            System.out.println("reservation = " + reservation);
            //예약 취소 검증
            ReservationStatus reservationStatus = concertRepository.findByReservationId(reservation.getId()).getReservationStatus();
            System.out.println("reservationStatus = " + reservationStatus);
            assertThat(reservationStatus).isEqualTo(ReservationStatus.CANCELED);

            //결제 취소 검증
            PaymentState paymentState = concertRepository.findPaymentByReservation(reservation).getPaymentState();
            System.out.println("paymentState = " + paymentState);
            assertThat(paymentState).isEqualTo(PaymentState.CANCELED);

            //좌석 예약 가능 상태 검증
            ConcertSeatStatus seatStatus = concertRepository.findBySeatId(reservation.getConcertSeatId()).getSeatStatus();
            System.out.println("seatStatus = " + seatStatus);
            assertThat(seatStatus).isEqualTo(ConcertSeatStatus.AVAILABLE);

            //대기열 토큰 만료 검증
            WaitingTokenStatus waitingTokenStatus = waitingTokenRepository.findUsingTokenByUserId(reservation.getUserId()).getWaitingTokenStatus();
            System.out.println("waitingTokenStatus = " + waitingTokenStatus);
            assertThat(waitingTokenStatus).isEqualTo(WaitingTokenStatus.EXPIRED);
        }
    }
}
