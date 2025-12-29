package org.informatics.transportcompany.model.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.informatics.transportcompany.model.enums.VehicleType;

@NoArgsConstructor
@Getter
@Setter
public class VehicleCreateRequest {
    @NotNull(message = "Company ID is required.")
    @Positive(message = "Company ID must be a positive number.")
    private long companyId;

    @NotBlank(message = "Registration number is required.")
    @Size(max = 20, message = "Registration number cannot be longer than 20 characters.")
    private String registrationNumber;

    @NotNull(message = "Vehicle type is required.")
    private VehicleType type;

    @NotNull(message = "Capacity is required.")
    @PositiveOrZero(message = "Capacity cannot be negative.")
    private int capacity;
}
