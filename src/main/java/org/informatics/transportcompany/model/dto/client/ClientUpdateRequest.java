package org.informatics.transportcompany.model.dto.client;

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
public class ClientUpdateRequest {

    @NotNull(message = "Client ID is required.")
    @Positive(message = "Client ID cannot be negative.")
    private Long id;

    @NotBlank(message = "Client name is required.")
    @Size(max = 100, message = "Client name cannot be longer than 100 characters.")
    private String name;

    @Size(max = 255, message = "Contact details cannot be longer than 255 characters.")
    private String contactDetails;
}

