package org.informatics.transportcompany.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transport_companies")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransportCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Transport company name is required.")
    @Size(max = 100, message = "Transport company name cannot be longer than 100 characters.")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 255, message = "Address cannot be longer than 255 characters.")
    private String address;
}
