package com.serviq.provider.annotation;

import com.serviq.provider.validator.TimeRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeRangeValidator.class)
@Documented
public @interface ValidTimeRange {
    String message() default "End time must be after start time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean allowNull() default false;
}
