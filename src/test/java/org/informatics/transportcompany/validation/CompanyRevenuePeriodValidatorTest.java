package org.informatics.transportcompany.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.informatics.transportcompany.model.dto.transport.CalculateCompanyRevenueForPeriodRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompanyRevenuePeriodValidatorTest {

    @Test
    void whenFromAfterTo_thenViolationIsOnFromField() {
        CalculateCompanyRevenueForPeriodRequest request = new CalculateCompanyRevenueForPeriodRequest();
        request.setCompanyId(1);
        request.setFrom(LocalDateTime.of(2020, 2, 2, 0, 0));
        request.setTo(LocalDateTime.of(2020, 1, 1, 0, 0));

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            Set<ConstraintViolation<CalculateCompanyRevenueForPeriodRequest>> violations = validator.validate(request);

            assertEquals(1, violations.size());

            ConstraintViolation<CalculateCompanyRevenueForPeriodRequest> v = violations.iterator().next();
            assertEquals("from", v.getPropertyPath().toString());
            assertEquals("Start date/time cannot be after the end date/time.", v.getMessage());
        }
    }
}
