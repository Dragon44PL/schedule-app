package com.github.schedule.workmonth.validation.handler;

import com.github.schedule.workmonth.exception.AccountNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class ApplicationExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<?> handleAccountNotFoundException(AccountNotFoundException exception) {
        final ErrorMessage errorMessage = new ErrorMessage("account.id", exception.getMessage());
        return ResponseEntity.badRequest().body(errorMessage);
    }
}
