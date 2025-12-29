package org.informatics.transportcompany.model.dto.transport;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = CompanyRevenuePeriodValidator.class)
public @interface ValidCompanyRevenuePeriod {

    String message() default "Start date/time cannot be after the end date/time.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
