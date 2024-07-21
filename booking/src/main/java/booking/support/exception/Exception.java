package booking.support.exception;

import org.springframework.http.HttpStatus;

public interface Exception {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
