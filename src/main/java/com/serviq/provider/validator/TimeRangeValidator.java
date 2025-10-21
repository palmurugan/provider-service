package com.serviq.provider.validator;

import com.serviq.provider.annotation.ValidTimeRange;
import com.serviq.provider.dto.request.AvailabilityConfigCreateRequest;
import com.serviq.provider.dto.request.AvailabilityConfigUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, Object> {

    private boolean allowNull;

    @Override
    public void initialize(ValidTimeRange constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalTime startTime = null;
        LocalTime endTime = null;

        if (value instanceof AvailabilityConfigCreateRequest request) {
            startTime = request.getStartTime();
            endTime = request.getEndTime();
        } else if (value instanceof AvailabilityConfigUpdateRequest request) {
            startTime = request.getStartTime();
            endTime = request.getEndTime();
        }

        if (startTime == null || endTime == null) {
            return allowNull;
        }

        return endTime.isAfter(startTime);
    }
}
