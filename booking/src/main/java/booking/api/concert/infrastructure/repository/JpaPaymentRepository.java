package booking.api.concert.infrastructure.repository;

import booking.api.concert.infrastructure.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, Long> {

    PaymentEntity findByReservationId(Long reservationId);
}
