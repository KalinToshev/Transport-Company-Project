package org.informatics.transportcompany.model.dto.transport;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@ValidCompanyRevenuePeriod
@NoArgsConstructor
@Getter
@Setter
public class CalculateCompanyRevenueForPeriodRequest {
    @NotNull(message = "Company ID is required")
    @Positive(message = "Company ID must be a positive number.")
    private long companyId;

    @NotNull(message = "From date-time is required")
    private LocalDateTime from;

    @NotNull(message = "To date-time is required")
    private LocalDateTime to;
}
