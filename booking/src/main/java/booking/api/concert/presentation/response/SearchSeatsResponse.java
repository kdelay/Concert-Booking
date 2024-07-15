package booking.api.concert.presentation.response;

import booking.api.concert.domain.enums.ConcertSeatStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record SearchSeatsResponse(
        int seatNumber, //좌석 번호
        BigDecimal price, //좌석 금액
        ConcertSeatStatus status //좌석 상태
) {
    public static List<SearchSeatsResponse> of(List<List<Object>> seatsInfo) {
        List<SearchSeatsResponse> result = new ArrayList<>();

        for (List<Object> details : seatsInfo) {
            int seatNumber = (int) details.get(0);
            BigDecimal price = (BigDecimal) details.get(1);
            ConcertSeatStatus status = ConcertSeatStatus.valueOf((String) details.get(2)); // 문자열을 열거형으로 변환

            result.add(new SearchSeatsResponse(seatNumber, price, status));
        }
        return result;
    }
}