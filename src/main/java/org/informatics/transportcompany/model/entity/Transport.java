package org.informatics.transportcompany.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transports")
@NoArgsConstructor
@Getter
@Setter
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Transport must be associated with a transport company.")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private TransportCompany company;

    @NotNull(message = "Transport must be associated with a client.")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @NotNull(message = "Transport must be associated with a vehicle.")
    @ManyToOne(optional = false, fetch =  FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @NotNull(message = "Transport must be associated with a driver.")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Employee driver;

    @NotBlank(message = "Departure address is required.")
    @Size(max = 255, message = "Departure address cannot be longer than 255 characters.")
    @Column(nullable = false)
    private String fromLocation;

    @NotBlank(message = "Arrival address is required.")
    @Size(max = 255, message = "Arrival address cannot be longer than 255 characters.")
    @Column(nullable = false)
    private String toLocation;

    @NotNull(message = "Departure date and time are required.")
    @Column(nullable = false)
    private LocalDateTime departureDateTime;

    @NotNull(message = "Arrival date and time are required.")
    @Column(nullable = false)
    private LocalDateTime arrivalDateTime;

    @Size(max = 500, message = "Cargo description cannot be longer than 500 characters.")
    @Column( length = 500 )
    private String cargoDescription;

    @PositiveOrZero(message = "Cargo weight cannot be negative.")
    private Double cargoWeight;

    @NotNull(message = "Transport price is required.")
    @PositiveOrZero(message = "Transport price cannot be negative.")
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull(message = "Payment status is required.")
    @Column(nullable = false)
    private boolean paid;
}
