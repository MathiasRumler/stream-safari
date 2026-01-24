package mvp.streamy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionsHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomErrorResponse> handleDisabledException(IllegalArgumentException e) {
        return generateErrorResponse(HttpStatus.BAD_REQUEST, IllegalArgumentException.class.getSimpleName(), e);
    }

    private ResponseEntity<CustomErrorResponse> generateErrorResponse(HttpStatus httpStatus, String exceptionName, Exception e) {
        String stackTrace = e.getMessage();
        log.debug("There was an {} during runtime. Stacktrace: {}", exceptionName, stackTrace);

        return ResponseEntity.status(httpStatus).body(new CustomErrorResponse( exceptionName, stackTrace));
    }

    private ResponseEntity<CustomErrorResponse> generateErrorResponse(HttpStatus httpStatus, String exceptionName,
                                                                      String customErrorMessaage) {
        if (log.isDebugEnabled()) {
            log.error("There was an {} during runtime. Errormessage: {}", exceptionName, customErrorMessaage);
        }
        return ResponseEntity.status(httpStatus).body(new CustomErrorResponse( exceptionName, customErrorMessaage));
    }
}
