package booking.support.handler;

import booking.support.exception.BaseException;
import booking.support.exception.CustomBadRequestException;
import booking.support.exception.CustomNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", e.getMessage()));
    }

    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<ErrorResponse> handleException(BaseException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    //--------------------------------------------------------------------------------------------------------------

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomBadRequestException.class)
    public ApiResultResponse<Object> handleCustomBadRequestException(CustomBadRequestException e) {
        return ApiResultResponse.of(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    public ApiResultResponse<Object> handleCustomNotFoundException(CustomNotFoundException e) {
        return ApiResultResponse.of(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }
}