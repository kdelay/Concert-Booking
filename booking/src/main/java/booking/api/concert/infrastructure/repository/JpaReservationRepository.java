package booking.api.concert.infrastructure.repository;

import booking.api.concert.domain.enums.ReservationStatus;
import booking.api.concert.infrastructure.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaReservationRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findAllByReservationStatus(ReservationStatus reservationStatus);
}
