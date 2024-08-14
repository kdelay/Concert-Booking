package booking.api.concert.infrastructure.db.repository;

import booking.api.concert.infrastructure.db.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, Long> {

    PaymentEntity findByReservationId(Long reservationId);
}
