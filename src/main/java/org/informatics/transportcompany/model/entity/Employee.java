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
import org.informatics.transportcompany.model.enums.EmployeeQualification;

import java.math.BigDecimal;

@Entity
@Table(name = "employees")
@NoArgsConstructor
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required.")
    @Size(max = 100, message = "First name must not exceed 100 characters.")
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 100, message = "Last name must not exceed 100 characters.")
    @Column(nullable = false, length = 100)
    private String lastName;

    @NotNull(message = "Qualification is required.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeQualification qualification;

    @NotNull(message = "Salary is required.")
    @PositiveOrZero(message = "Salary cannot be negative.")
    @Column(nullable = false)
    private BigDecimal salary;

    @NotNull(message = "Employee must be associated with a transport company.")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private TransportCompany company;
}
