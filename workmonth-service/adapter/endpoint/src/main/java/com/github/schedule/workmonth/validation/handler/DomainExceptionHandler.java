package com.github.schedule.workmonth.validation.handler;

import com.github.schedule.workmonth.exception.WorkMonthExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class DomainExceptionHandler {

    @ExceptionHandler(WorkMonthExistsException.class)
    ResponseEntity<?> handleWorkMonthExistsException() {
        final String message = "WorkMonth with given UserId and YearMonth already exists";
        final ErrorMessage errorMessage = new ErrorMessage("workmonth.user", message);
        return ResponseEntity.badRequest().body(errorMessage);
    }
}
