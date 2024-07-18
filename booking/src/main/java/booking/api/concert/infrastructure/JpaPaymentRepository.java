package booking.api.concert.infrastructure;

import booking.api.concert.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, Long> {

    PaymentEntity findByReservationEntity(Reservation reservation);
}
