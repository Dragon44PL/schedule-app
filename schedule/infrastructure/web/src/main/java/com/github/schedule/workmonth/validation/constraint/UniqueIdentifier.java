package com.github.schedule.workmonth.validation.constraint;

import com.github.schedule.workmonth.validation.UUIDValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Cause(causedBy = "uuid.format")
@Constraint(validatedBy = UUIDValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueIdentifier {
    String message() default "UUID format not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}