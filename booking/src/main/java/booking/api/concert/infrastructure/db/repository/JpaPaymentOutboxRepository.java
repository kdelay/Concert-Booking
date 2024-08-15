package booking.api.concert.infrastructure.db.repository;

import booking.api.concert.domain.enums.PaymentOutboxState;
import booking.api.concert.infrastructure.db.PaymentOutboxEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaPaymentOutboxRepository extends JpaRepository<PaymentOutboxEntity, Long> {

    @Query("SELECT po FROM PaymentOutboxEntity po WHERE po.uuid = :uuid")
    PaymentOutboxEntity findByUuid(@Param("uuid") String uuid);

    @Query("SELECT po FROM PaymentOutboxEntity po WHERE po.paymentOutboxState = :paymentOutboxState")
    List<PaymentOutboxEntity> findAllByStatus(@Param("paymentOutboxState") PaymentOutboxState paymentOutboxState);
}