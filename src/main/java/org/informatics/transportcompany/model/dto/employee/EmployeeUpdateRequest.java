package org.informatics.transportcompany.model.dto.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.informatics.transportcompany.model.enums.EmployeeQualification;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class EmployeeUpdateRequest {
    @NotNull(message = "Employee ID is required.")
    @Positive(message = "Employee ID must be a positive number.")
    private long id;

    @NotBlank(message = "First name is required.")
    @Size(max = 100, message = "First name must not exceed 100 characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 100, message = "Last name must not exceed 100 characters.")
    private String lastName;

    @NotNull(message = "Qualification is required.")
    private EmployeeQualification qualification;

    @NotNull(message = "Salary is required.")
    @PositiveOrZero(message = "Salary cannot be negative.")
    private BigDecimal salary;
}
