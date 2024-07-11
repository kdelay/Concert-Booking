package booking.api.concert.infrastructure;

import booking.api.concert.domain.Concert;
import booking.api.concert.domain.ConcertSchedule;
import booking.api.concert.domain.ConcertSeat;

import java.util.List;
import java.util.stream.Collectors;

public class ConcertMapper {

    public static ConcertSchedule scheduleToDomain(ConcertScheduleEntity entity) {
        return ConcertSchedule.create(entity.getId(), ConcertMapper.toDomain(entity.getConcertEntity()), entity.getConcertDate());
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

    public static ConcertSeat seatToDomain(ConcertSeatEntity entity) {
        return ConcertSeat.create(entity.getId(), ConcertMapper.toDomain(entity.getConcertEntity()),
                ConcertMapper.scheduleToDomain(entity.getConcertScheduleEntity()), entity.getUserId(),
                entity.getSeatNumber(), entity.getSeatPrice(), entity.getSeatStatus(), entity.getModifiedAt(), entity.getExpiredAt());
    }

    public static ConcertSeatEntity seatToEntity(ConcertSeat concertSeat) {
        return ConcertSeatEntity.builder()
                .id(concertSeat.getId())
                .concertEntity(ConcertMapper.toEntity(concertSeat.getConcert()))
                .concertScheduleEntity(ConcertMapper.scheduleToEntity(concertSeat.getConcertSchedule()))
                .userId(concertSeat.getUserId())
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
}
