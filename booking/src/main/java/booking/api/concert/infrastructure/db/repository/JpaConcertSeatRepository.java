package booking.api.concert.infrastructure.db.repository;

import booking.api.concert.infrastructure.db.ConcertEntity;
import booking.api.concert.infrastructure.db.ConcertScheduleEntity;
import booking.api.concert.infrastructure.db.ConcertSeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaConcertSeatRepository extends JpaRepository<ConcertSeatEntity, Long> {

    List<ConcertSeatEntity> findByConcertEntityAndConcertScheduleEntity(ConcertEntity concertEntity, ConcertScheduleEntity concertScheduleEntity);

    @Query("select cs from ConcertSeatEntity cs " +
            "where cs.concertEntity.id = :concertId " +
            "and cs.concertScheduleEntity.id = :concertScheduleId " +
            "and cs.seatNumber = :seatNumber")
    Optional<ConcertSeatEntity> findSeatsBySeatNumber(
            @Param("concertId") Long concertId,
            @Param("concertScheduleId") Long concertScheduleId,
            @Param("seatNumber") int seatNumber);
}