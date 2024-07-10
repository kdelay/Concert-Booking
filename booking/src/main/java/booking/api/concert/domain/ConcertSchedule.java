package booking.api.concert.domain;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ConcertSchedule {

    private final Long id;
    private final Concert concert;
    private final LocalDate concertDate;

    public ConcertSchedule(Long id, Concert concert, LocalDate concertDate) {
        if (concert == null) throw new IllegalArgumentException("[ConcertSchedule - concert] is null");
        if (concertDate == null) throw new IllegalArgumentException("[ConcertSchedule - concertDate] is null");
        this.id = id;
        this.concert = concert;
        this.concertDate = concertDate;
    }

    public static ConcertSchedule create(Long id, Concert concert, LocalDate concertDate) {
        return new ConcertSchedule(id, concert, concertDate);
    }
}