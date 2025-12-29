package org.informatics.transportcompany.model.dto.transport;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CompanyRevenuePeriodValidator implements ConstraintValidator<ValidCompanyRevenuePeriod, CalculateCompanyRevenueForPeriodRequest> {

    @Override
    public void initialize(ValidCompanyRevenuePeriod constraintAnnotation) {
        // _
    }

    @Override
    public boolean isValid(CalculateCompanyRevenueForPeriodRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        var from = value.getFrom();
        var to = value.getTo();

        if (from == null || to == null) {
            return true;
        }

        if (from.isAfter(to)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("from")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

