package com.github.schedule.workmonth.validation;

import com.github.schedule.workmonth.validation.constraint.uuid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class UuidValidator implements ConstraintValidator<uuid, String> {

    @Override
    public boolean isValid(String uuid, ConstraintValidatorContext constraintValidatorContext) {
        try{
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException exception){
            return false;
        }
    }
}
