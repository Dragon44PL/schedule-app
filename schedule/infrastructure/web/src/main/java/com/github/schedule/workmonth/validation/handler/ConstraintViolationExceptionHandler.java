package com.github.schedule.workmonth.validation.handler;

import com.github.schedule.workmonth.validation.constraint.Cause;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.annotation.Annotation;
import java.util.stream.Collectors;

@ControllerAdvice
class ConstraintViolationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException exception) {
        var errors = exception.getConstraintViolations().stream().map(this::createErrorMessage).collect(Collectors.toSet());
        return ResponseEntity.badRequest().body(errors);
    }

    ErrorMessage createErrorMessage(ConstraintViolation<?> constraintViolation) {
        final String message = constraintViolation.getMessage();
        final String cause = resolveCause(constraintViolation.getConstraintDescriptor().getAnnotation());
        return new ErrorMessage(cause, message);
    }

    private String resolveCause(Annotation annotation) { ;
        final Cause cause = annotation.annotationType().getAnnotation(Cause.class);
        return cause != null ? cause.causedBy() : "";
    }
}
