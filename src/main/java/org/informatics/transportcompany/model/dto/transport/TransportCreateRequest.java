package org.informatics.transportcompany.model.dto.transport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class TransportCreateRequest {
    @NotNull(message = "Company ID is required.")
    @Positive(message = "Company ID must be a positive number.")
    private long companyId;

    @NotNull(message = "Client ID is required.")
    @Positive(message = "Client ID must be a positive number.")
    private long clientId;

    @NotNull(message = "Vehicle ID is required.")
    @Positive(message = "Vehicle ID must be a positive number.")
    private long vehicleId;

    @NotNull(message = "Driver ID is required.")
    @Positive(message = "Driver ID must be a positive number.")
    private long driverId;

    @NotBlank(message = "Departure address is required.")
    @Size(max = 255, message = "Departure address cannot be longer than 255 characters.")
    private String fromLocation;

    @NotBlank(message = "Arrival address is required.")
    @Size(max = 255, message = "Arrival address cannot be longer than 255 characters.")
    private String toLocation;

    @NotNull(message = "Departure date and time are required.")
    private LocalDateTime departure;

    @NotNull(message = "Arrival date and time are required.")
    private LocalDateTime arrival;

    @Size(max = 500, message = "Cargo description cannot be longer than 500 characters.")
    private String cargoDescription;

    @PositiveOrZero(message = "Cargo weight cannot be negative.")
    private Double cargoWeight;

    @NotNull(message = "Transport price is required.")
    @PositiveOrZero(message = "Transport price cannot be negative.")
    private BigDecimal price;

    @NotNull(message = "Payment status is required.")
    private boolean paid;
}
