package booking.support.scheduler;

import booking.api.concert.domain.Payment;
import booking.api.concert.domain.PaymentOutbox;
import booking.api.concert.domain.Reservation;
import booking.api.concert.domain.enums.PaymentState;
import booking.api.concert.domain.enums.ReservationStatus;
import booking.api.concert.domain.event.PaymentSuccessEvent;
import booking.api.concert.domain.message.PaymentMessage;
import booking.api.concert.domain.message.PaymentMessageOutbox;
import booking.support.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static booking.api.concert.domain.enums.PaymentOutboxState.INIT;
import static booking.api.concert.domain.enums.PaymentOutboxState.PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class KafkaRepublishSchedulerTest {

    @Autowired
    public PaymentMessageOutbox paymentMessageOutbox;

    @Autowired
    public KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaRepublishScheduler kafkaRepublishScheduler;

    @Autowired
    private ScheduledAnnotationBeanPostProcessor postProcessor;

    @BeforeEach
    void setUp() {
        //스케줄러 초기화
        postProcessor.postProcessAfterInitialization(kafkaRepublishScheduler, "kafkaRepublishScheduler");
    }

    @Test
    @DisplayName("스케줄러가 동작한 후의 아웃박스 상태는 PUBLISHED 상태여야 한다.")
    void republishTest() throws InterruptedException {
        Reservation reservation = new Reservation(1L, 1L, 1L, "Concert 1",
                LocalDate.parse("2024-08-15"), ReservationStatus.RESERVING, LocalDateTime.now(), null);
        Payment payment = new Payment(1L, 1L, BigDecimal.valueOf(1000), PaymentState.PENDING, LocalDateTime.now(), null);
        String token = "f8d007a8-7459-480d-8434-cd3690763499";

        String uuid = "test";
        PaymentSuccessEvent event = new PaymentSuccessEvent(reservation, payment, token);
        PaymentMessage<PaymentSuccessEvent> message = new PaymentMessage<>(uuid, event);
        String payload = JsonUtil.toJson(message);

        //아웃박스 생성
        PaymentOutbox paymentOutbox = paymentMessageOutbox.save(message);
        paymentOutbox.setTenMinAgo(); //10분 전 데이터로 설정
        paymentMessageOutbox.entitySave(paymentOutbox);

        //카프카 메시지 발행
        kafkaTemplate.send("payment-success", payload).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("[Kafka - Send] Failed to send payload. Error: {}", exception.getMessage(), exception);
            } else {
                log.info("[Kafka - Send] Payload sent successfully. Payload: {}", payload);
            }
        });

        //스케줄러 동작 전 INIT 상태
        assertThat(paymentOutbox.getPaymentOutboxState()).isEqualTo(INIT);

        //스케줄러 호출
        kafkaRepublishScheduler.republish();

        //5초 대기
        Thread.sleep(5000);

        //스케줄러 동작 후 PUBLISHED 상태
        PaymentOutbox updatedOutbox = paymentMessageOutbox.findByUuid(paymentOutbox.getUuid());
        assertThat(updatedOutbox.getPaymentOutboxState()).isEqualTo(PUBLISHED);
    }

}