package booking.api.concert.infrastructure;

import booking.api.concert.domain.Concert;

public class ConcertMapper {

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
