package org.informatics.transportcompany.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.informatics.transportcompany.exceptions.Transport.NoTransportWithProvidedIdException;
import org.informatics.transportcompany.exceptions.client.NoClientWithProvidedIdException;
import org.informatics.transportcompany.exceptions.company.NoCompanyWithProvidedIdException;
import org.informatics.transportcompany.exceptions.employee.NoEmployeeWithProvidedIdException;
import org.informatics.transportcompany.exceptions.vehicle.NoVehicleWithProvidedIdException;
import org.informatics.transportcompany.model.dto.transport.CalculateCompanyRevenueForPeriodRequest;
import org.informatics.transportcompany.model.dto.transport.TransportCreateRequest;
import org.informatics.transportcompany.model.entity.Client;
import org.informatics.transportcompany.model.entity.Employee;
import org.informatics.transportcompany.model.entity.Transport;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.model.entity.Vehicle;
import org.informatics.transportcompany.repository.client.ClientRepository;
import org.informatics.transportcompany.repository.employee.EmployeeRepository;
import org.informatics.transportcompany.repository.transport.TransportRepository;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;
import org.informatics.transportcompany.repository.vehicle.VehicleRepository;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class TransportService {

    private final TransportRepository transportRepository;
    private final TransportCompanyRepository transportCompanyRepository;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final EmployeeRepository employeeRepository;

    public Transport createTransport(TransportCreateRequest request) {
        TransportCompany company = transportCompanyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new NoCompanyWithProvidedIdException("No company with id = " + request.getCompanyId()));

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new NoClientWithProvidedIdException("No client with id = " + request.getClientId()));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new NoVehicleWithProvidedIdException("No vehicle with id = " + request.getVehicleId()));

        Employee driver = employeeRepository.findById(request.getDriverId())
                .orElseThrow(() -> new NoEmployeeWithProvidedIdException("No employee (driver) with id = " + request.getDriverId()));

        Transport t = new Transport();
        t.setCompany(company);
        t.setClient(client);
        t.setVehicle(vehicle);
        t.setDriver(driver);
        t.setFromLocation(request.getFromLocation());
        t.setToLocation(request.getToLocation());
        t.setDepartureDateTime(request.getDeparture());
        t.setArrivalDateTime(request.getArrival());
        t.setCargoDescription(request.getCargoDescription());
        t.setCargoWeight(request.getCargoWeight());
        t.setPrice(request.getPrice());
        t.setPaid(request.isPaid());

        return transportRepository.create(t);
    }

    public List<Transport> findAll() {
        return transportRepository.findAllWithAllJoins();
    }

    public List<Transport> findAllOrderByToLocation() {
        return transportRepository.findAllOrderByToLocationWithClient();
    }

    public List<Transport> findByToLocation(
            @NotBlank(message = "Destination cannot be empty.")
            String toLocation
    ) {
        return transportRepository.findByToLocationWithClient(toLocation);
    }

    public Transport markPaid(long id) {
        Transport t = transportRepository.findByIdWithClient(id)
                .orElseThrow(() -> new NoTransportWithProvidedIdException("No transport with id = " + id));

        t.setPaid(true);
        return transportRepository.update(t);
    }

    public long countAllTransports() {
        return transportRepository.countAll();
    }

    public BigDecimal calculateTotalRevenue() {
        return transportRepository.sumTotalRevenue();
    }

    public List<Object[]> findDriverTransportStats() {
        return transportRepository.driverTransportStats();
    }

    public BigDecimal calculateCompanyRevenueForPeriod(CalculateCompanyRevenueForPeriodRequest request) {
        TransportCompany company = transportCompanyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new NoCompanyWithProvidedIdException("No company with id = " + request.getCompanyId()));

        return transportRepository.sumCompanyRevenueForPeriod(company, request.getFrom(), request.getTo());
    }

    public List<Object[]> findDriverRevenue() {
        return transportRepository.driverRevenue();
    }
}