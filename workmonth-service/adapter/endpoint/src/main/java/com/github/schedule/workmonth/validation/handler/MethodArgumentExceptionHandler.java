package com.github.schedule.workmonth.validation.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class MethodArgumentExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        final String name = exception.getParameter().getParameterName();
        final ErrorMessage errorMessage = new ErrorMessage("%s.type".formatted(name), "Invalid field type");
        return ResponseEntity.badRequest().body(errorMessage);
    }
}
