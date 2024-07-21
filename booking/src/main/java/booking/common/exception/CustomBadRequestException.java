package booking.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomBadRequestException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String msg;

    @Override
    public String getMessage() {
        return "[%s] %s".formatted(errorCode, msg);
    }
}
