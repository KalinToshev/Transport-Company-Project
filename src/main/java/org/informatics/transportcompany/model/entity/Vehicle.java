package org.informatics.transportcompany.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.informatics.transportcompany.model.enums.VehicleType;

@Entity
@Table(name = "vehicles")
@NoArgsConstructor
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Registration number is required.")
    @Size(max = 20, message = "Registration number cannot be longer than 20 characters.")
    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @NotNull(message = "Vehicle type is required.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @NotNull(message = "Capacity is required.")
    @PositiveOrZero(message = "Capacity cannot be negative.")
    @Column(nullable = false)
    private int capacity;

    @NotNull(message = "Vehicle must be associated with a transport company.")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private TransportCompany company;
}
