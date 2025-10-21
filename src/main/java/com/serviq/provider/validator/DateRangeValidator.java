package com.serviq.provider.validator;

import com.serviq.provider.annotation.ValidDateRange;
import com.serviq.provider.dto.request.AvailabilityConfigCreateRequest;
import com.serviq.provider.dto.request.AvailabilityConfigUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private boolean allowNull;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate startDate = null;
        LocalDate endDate = null;

        if (value instanceof AvailabilityConfigCreateRequest request) {
            startDate = request.getStartDate();
            endDate = request.getEndDate();
        } else if (value instanceof AvailabilityConfigUpdateRequest request) {
            startDate = request.getStartDate();
            endDate = request.getEndDate();
        }

        if (startDate == null || endDate == null) {
            return allowNull;
        }

        return !endDate.isBefore(startDate);
    }
}
