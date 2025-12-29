package org.informatics.transportcompany.model.dto.transportCompany;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TransportCompanyUpdateRequest {
    @NotNull(message = "Transport company ID is required.")
    @Positive(message = "Transport company ID must be a positive number.")
    private long id;

    @NotBlank(message = "Transport company name is required.")
    @Size(max = 100, message = "Transport company name cannot be longer than 100 characters.")
    private String name;

    @Size(max = 255, message = "Address cannot be longer than 255 characters.")
    private String address;
}
