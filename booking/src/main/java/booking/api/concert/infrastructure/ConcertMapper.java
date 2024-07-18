package booking.api.concert.infrastructure;

import booking.api.concert.domain.Payment;
import booking.api.concert.domain.Concert;
import booking.api.concert.domain.ConcertSchedule;
import booking.api.concert.domain.ConcertSeat;
import booking.api.concert.domain.Reservation;

import java.util.List;
import java.util.stream.Collectors;

public class ConcertMapper {

    //concert schedule
    public static ConcertSchedule scheduleToDomain(ConcertScheduleEntity entity) {
        return ConcertSchedule.create(entity.getId(),
                ConcertMapper.toDomain(entity.getConcertEntity()),
                entity.getConcertDate());
    }

    public static ConcertScheduleEntity scheduleToEntity(ConcertSchedule concertSchedule) {
        return ConcertScheduleEntity.builder()
                .id(concertSchedule.getId())
                .concertEntity(ConcertMapper.toEntity(concertSchedule.getConcert()))
                .concertDate(concertSchedule.getConcertDate())
                .build();
    }

    public static List<ConcertSchedule> scheduleToDomainList(List<ConcertScheduleEntity> entities) {
        return entities.stream()
                .map(ConcertMapper::scheduleToDomain)
                .collect(Collectors.toList());
    }

    //concert seat
    public static ConcertSeat seatToDomain(ConcertSeatEntity entity) {
        return new ConcertSeat(entity.getId(),
                ConcertMapper.toDomain(entity.getConcertEntity()),
                ConcertMapper.scheduleToDomain(entity.getConcertScheduleEntity()),
                entity.getUserId(),
                entity.getSeatNumber(),
                entity.getSeatPrice(),
                entity.getSeatStatus(),
                entity.getModifiedAt(),
                entity.getExpiredAt());
    }

    public static ConcertSeatEntity seatToEntity(ConcertSeat concertSeat) {
        return ConcertSeatEntity.builder()
                .id(concertSeat.getId())
                .concertEntity(ConcertMapper.toEntity(concertSeat.getConcert()))
                .concertScheduleEntity(ConcertMapper.scheduleToEntity(concertSeat.getConcertSchedule()))
                .userId(concertSeat.getUserId())
                .seatNumber(concertSeat.getSeatNumber())
                .seatPrice(concertSeat.getSeatPrice())
                .seatStatus(concertSeat.getSeatStatus())
                .modifiedAt(concertSeat.getModifiedAt())
                .expiredAt(concertSeat.getModifiedAt())
                .build();
    }

    public static List<ConcertSeat> seatToDomainList(List<ConcertSeatEntity> entities) {
        return entities.stream()
                .map(ConcertMapper::seatToDomain)
                .collect(Collectors.toList());
    }

    //concert
    public static Concert toDomain(ConcertEntity entity) {
        return Concert.create(entity.getId(), entity.getName(), entity.getHost());
    }

    public static ConcertEntity toEntity(Concert concert) {
        return ConcertEntity.builder()
                .id(concert.getId())
                .name(concert.getName())
                .host(concert.getHost())
                .build();
    }

    //reservation
    public static Reservation reservationToDomain(ReservationEntity entity) {
        return new Reservation(entity.getId(), entity.getConcertSeatId(), entity.getUserId(), entity.getConcertName(),
                entity.getConcertDate(), entity.getReservationStatus(), entity.getCreatedAt(), entity.getModifiedAt());
    }

    public static ReservationEntity reservationToEntity(Reservation reservation) {
        return ReservationEntity.builder()
                .id(reservation.getId())
                .concertSeatId(reservation.getConcertSeatId())
                .userId(reservation.getUserId())
                .concertName(reservation.getConcertName())
                .concertDate(reservation.getConcertDate())
                .reservationStatus(reservation.getReservationStatus())
                .createdAt(reservation.getCreatedAt())
                .modifiedAt(reservation.getModifiedAt())
                .build();
    }

    /**
     * @param entities 예약 Entity
     * @return Domain List
     */
    public static List<Reservation> reservationToDomainList(List<ReservationEntity> entities) {
        return entities.stream()
                .map(ConcertMapper::reservationToDomain)
                .collect(Collectors.toList());
    }

    //payment
    public static Payment paymentToDomain(PaymentEntity entity) {
        return new Payment(entity.getId(), ConcertMapper.reservationToDomain(entity.getReservationEntity()), entity.getPrice(),
                entity.getPaymentState(), entity.getCreatedAt(), entity.getModifiedAt());
    }

    public static PaymentEntity paymentToEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId())
                .reservationEntity(ConcertMapper.reservationToEntity(payment.getReservation()))
                .price(payment.getPrice())
                .paymentState(payment.getPaymentState())
                .createdAt(payment.getCreatedAt())
                .modifiedAt(payment.getModifiedAt())
                .build();
    }
}
