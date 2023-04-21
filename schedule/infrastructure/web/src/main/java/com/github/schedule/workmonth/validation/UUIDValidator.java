package com.github.schedule.workmonth.validation;

import com.github.schedule.workmonth.validation.constraint.UniqueIdentifier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class UUIDValidator implements ConstraintValidator<UniqueIdentifier, String> {

    @Override
    public void initialize(UniqueIdentifier uniqueIdentifier) { }

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
