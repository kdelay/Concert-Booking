package booking.common.handler;

import booking.common.exception.AuthorizationException;
import booking.common.exception.BaseException;
import booking.common.exception.CustomBadRequestException;
import booking.common.exception.CustomNotFoundException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @ExceptionHandler(value = AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleException(AuthorizationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("401", ex.getMessage()));
    }

    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<ErrorResponse> handleException(BaseException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    //--------------------------------------------------------------------------------------------------------------

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomBadRequestException.class)
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ApiResultResponse<Object> handleCustomBadRequestException(CustomBadRequestException e) {
        return ApiResultResponse.of(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Not Found")
    public ApiResultResponse<Object> handleCustomNotFoundException(CustomNotFoundException e) {
        return ApiResultResponse.of(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }
}