package booking.api.concert.integration;

import booking.api.concert.domain.Payment;
import booking.api.concert.domain.Reservation;
import booking.api.concert.domain.enums.PaymentState;
import booking.api.concert.domain.enums.ReservationStatus;
import booking.api.concert.domain.event.PaymentSuccessEvent;
import booking.api.concert.domain.message.PaymentMessage;
import booking.support.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@TestPropertySource(
        properties = {
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.datasource.url=jdbc:h2:mem:test;MODE=MySQL;NON_KEYWORDS=USER",
                "payment.topic-name=payment-success",
                "spring.async.execution.pool.core-size=5",
                "spring.async.execution.pool.max-size=10",
                "spring.async.execution.pool.queue-capacity=25"
        }
)
@Testcontainers
@Slf4j
public class KafkaPublisherTest {

    @Container
    static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("apache/kafka:3.7.0"));

    @Container
    static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(
            DockerImageName.parse("mysql:latest"));

    @Container
    static final GenericContainer<?> redisContainer = new GenericContainer<>(
            DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.consumer.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);

        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    @Autowired
    public KafkaTemplate<String, String> kafkaTemplate;

    @Test
    @DisplayName("카프카 메시지 발행 성공 테스트")
    public void messageSendTest() {

        Reservation reservation = new Reservation(1L, 1L, 1L, "Concert 1",
                LocalDate.parse("2024-08-15"), ReservationStatus.RESERVING, LocalDateTime.now(), null);
        Payment payment = new Payment(1L, 1L, BigDecimal.valueOf(1000), PaymentState.PENDING, LocalDateTime.now(), null);
        String token = "f8d007a8-7459-480d-8434-cd3690763499";

        String uuid = "test";
        PaymentSuccessEvent event = new PaymentSuccessEvent(reservation, payment, token);
        PaymentMessage<PaymentSuccessEvent> message = new PaymentMessage<>(uuid, event);
        String payload = JsonUtil.toJson(message);

        kafkaTemplate.send("payment-success", payload).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("[Kafka - Send] Failed to send payload. Error: {}", exception.getMessage(), exception);
            } else {
                log.info("[Kafka - Send] Payload sent successfully. Payload: {}", payload);
            }
        });
    }
}