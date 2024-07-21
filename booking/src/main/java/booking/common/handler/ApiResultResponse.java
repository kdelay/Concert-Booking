package booking.common.handler;

import org.springframework.http.HttpStatus;

public record ApiResultResponse<T>(
        HttpStatus httpStatus,
        String msg,
        T data
) {
    public static <T> ApiResultResponse<T> of(HttpStatus httpStatus, String msg, T data) {
        return new ApiResultResponse<>(httpStatus, msg, data);
    }

    public static <T> ApiResultResponse<T> of(HttpStatus httpStatus, T data) {
        return new ApiResultResponse<>(httpStatus, httpStatus.name(), data);
    }

    public static <T> ApiResultResponse<T> ok(T data) {
        return of(HttpStatus.OK, data);
    }
}
