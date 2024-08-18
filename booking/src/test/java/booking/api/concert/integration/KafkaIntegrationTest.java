package booking.api.concert.integration;

import booking.api.concert.domain.ConcertService;
import booking.api.concert.domain.PaymentOutbox;
import booking.api.concert.domain.enums.PaymentOutboxState;
import booking.api.concert.domain.message.PaymentMessageOutbox;
import booking.api.concert.interfaces.consumer.PaymentMessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class KafkaIntegrationTest {

    @Autowired
    public PaymentMessageOutbox paymentMessageOutbox;

    @Autowired
    public ConcertService concertService;

    @Autowired
    public PaymentMessageConsumer paymentMessageConsumer;

    @Test
    @DisplayName("카프카 발행 및 컨슘 후 아웃박스 PUBLISHED 상태 테스트")
    public void outboxSaveTest() throws InterruptedException {

        String token = "f8d007a8-7459-480d-8434-cd3690763499";

        concertService.pay(1L, 1L, token);
        List<PaymentOutbox> paymentOutboxList = paymentMessageOutbox.findAll();
        String uuid = paymentOutboxList.get(0).getUuid();

        Thread.sleep(2000);

        PaymentOutbox paymentOutbox = paymentMessageOutbox.findByUuid(uuid);
        assertThat(paymentOutbox.getPaymentOutboxState()).isEqualTo(PaymentOutboxState.PUBLISHED);
    }
}