package org.informatics.transportcompany.service;

import org.hibernate.SessionFactory;
import org.informatics.transportcompany.config.HibernateUtil;
import org.informatics.transportcompany.exceptions.Transport.NoTransportWithProvidedIdException;
import org.informatics.transportcompany.model.dto.transport.CalculateCompanyRevenueForPeriodRequest;
import org.informatics.transportcompany.model.dto.transport.TransportCreateRequest;
import org.informatics.transportcompany.model.entity.Client;
import org.informatics.transportcompany.model.entity.Employee;
import org.informatics.transportcompany.model.entity.Transport;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.model.entity.Vehicle;
import org.informatics.transportcompany.model.enums.EmployeeQualification;
import org.informatics.transportcompany.model.enums.VehicleType;
import org.informatics.transportcompany.repository.client.ClientRepository;
import org.informatics.transportcompany.repository.client.ClientRepositoryImpl;
import org.informatics.transportcompany.repository.employee.EmployeeRepository;
import org.informatics.transportcompany.repository.employee.EmployeeRepositoryImpl;
import org.informatics.transportcompany.repository.transport.TransportRepository;
import org.informatics.transportcompany.repository.transport.TransportRepositoryImpl;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepositoryImpl;
import org.informatics.transportcompany.repository.vehicle.VehicleRepository;
import org.informatics.transportcompany.repository.vehicle.VehicleRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransportServiceTest {

    private static SessionFactory sessionFactory;

    private TransportCompanyRepository companyRepository;
    private ClientRepository clientRepository;
    private VehicleRepository vehicleRepository;
    private EmployeeRepository employeeRepository;

    private TransportService service;

    @BeforeAll
    static void setup() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @BeforeEach
    void initTests() {
        sessionFactory.getSchemaManager().truncateMappedObjects();

        companyRepository = new TransportCompanyRepositoryImpl();
        clientRepository = new ClientRepositoryImpl();
        vehicleRepository = new VehicleRepositoryImpl();
        employeeRepository = new EmployeeRepositoryImpl();
        TransportRepository transportRepository = new TransportRepositoryImpl();

        service = new TransportService(
                transportRepository,
                companyRepository,
                clientRepository,
                vehicleRepository,
                employeeRepository
        );
    }

    @AfterEach
    void endTests() {
        sessionFactory.getSchemaManager().truncateMappedObjects();
    }

    @Test
    void whenMarkPaidWithMissingTransport_thenThrowNoTransportWithProvidedIdException() {
        assertThrows(NoTransportWithProvidedIdException.class, () -> service.markPaid(999));
    }

    @Test
    void givenTransport_whenMarkPaid_thenPaidIsTrue() {
        TransportCompany company = createCompany("Acme Logistics");
        Client client = createClient(company, "Client A");
        Vehicle vehicle = createVehicle(company, "CA1234AB");
        Employee driver = createDriver(company, "Ivan", "Ivanov");

        Transport created = service.createTransport(buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2020, 1, 10, 10, 0),
                LocalDateTime.of(2020, 1, 10, 18, 0),
                new BigDecimal("100.00"),
                false
        ));

        assertFalse(created.isPaid());

        Transport updated = service.markPaid(created.getId());

        assertTrue(updated.isPaid());
    }

    @Test
    void whenCalculateTotalRevenue_thenSumAllTransports() {
        TransportCompany company = createCompany("Acme Logistics");
        Client client = createClient(company, "Client A");
        Vehicle vehicle = createVehicle(company, "CA1234AB");
        Employee driver = createDriver(company, "Ivan", "Ivanov");

        service.createTransport(buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2020, 1, 10, 10, 0),
                LocalDateTime.of(2020, 1, 10, 18, 0),
                new BigDecimal("100.00"),
                true
        ));

        service.createTransport(buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2020, 2, 10, 10, 0),
                LocalDateTime.of(2020, 2, 10, 18, 0),
                new BigDecimal("200.00"),
                false
        ));

        BigDecimal total = service.calculateTotalRevenue();
        assertEquals(0, total.compareTo(new BigDecimal("300.00")));
    }

    @Test
    void whenCalculateCompanyRevenueForPeriod_thenCountsOnlyPaidAndWithinPeriod() {
        TransportCompany company = createCompany("Acme Logistics");
        Client client = createClient(company, "Client A");
        Vehicle vehicle = createVehicle(company, "CA1234AB");
        Employee driver = createDriver(company, "Ivan", "Ivanov");

        // included: paid + departure within period
        service.createTransport(buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2020, 3, 1, 10, 0),
                LocalDateTime.of(2020, 3, 1, 18, 0),
                new BigDecimal("100.00"),
                true
        ));

        // excluded: unpaid
        service.createTransport(buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2020, 3, 2, 10, 0),
                LocalDateTime.of(2020, 3, 2, 18, 0),
                new BigDecimal("200.00"),
                false
        ));

        // excluded: outside period
        service.createTransport(buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2020, 4, 10, 10, 0),
                LocalDateTime.of(2020, 4, 10, 18, 0),
                new BigDecimal("300.00"),
                true
        ));

        CalculateCompanyRevenueForPeriodRequest request = new CalculateCompanyRevenueForPeriodRequest();
        request.setCompanyId(company.getId());
        request.setFrom(LocalDateTime.of(2020, 3, 1, 0, 0));
        request.setTo(LocalDateTime.of(2020, 3, 31, 23, 59));

        BigDecimal revenue = service.calculateCompanyRevenueForPeriod(request);
        assertEquals(0, revenue.compareTo(new BigDecimal("100.00")));
    }

    @Test
    void whenFindDriverRevenue_thenReturnsPaidSumsOrderedDesc() {
        TransportCompany company = createCompany("Acme Logistics");
        Client client = createClient(company, "Client A");
        Vehicle vehicle = createVehicle(company, "CA1234AB");

        Employee driver1 = createDriver(company, "Ivan", "Ivanov");
        Employee driver2 = createDriver(company, "Petar", "Petrov");

        // driver1 paid sum = 300
        service.createTransport(buildTransportRequest(company, client, vehicle, driver1,
                LocalDateTime.of(2020, 1, 10, 10, 0),
                LocalDateTime.of(2020, 1, 10, 18, 0),
                new BigDecimal("100.00"),
                true
        ));
        service.createTransport(buildTransportRequest(company, client, vehicle, driver1,
                LocalDateTime.of(2020, 2, 10, 10, 0),
                LocalDateTime.of(2020, 2, 10, 18, 0),
                new BigDecimal("200.00"),
                true
        ));

        // driver2 paid sum = 400
        service.createTransport(buildTransportRequest(company, client, vehicle, driver2,
                LocalDateTime.of(2020, 3, 10, 10, 0),
                LocalDateTime.of(2020, 3, 10, 18, 0),
                new BigDecimal("400.00"),
                true
        ));

        // excluded from revenue: unpaid
        service.createTransport(buildTransportRequest(company, client, vehicle, driver2,
                LocalDateTime.of(2020, 3, 15, 10, 0),
                LocalDateTime.of(2020, 3, 15, 18, 0),
                new BigDecimal("999.00"),
                false
        ));

        List<Object[]> rows = service.findDriverRevenue();
        assertEquals(2, rows.size());

        Employee topDriver = (Employee) rows.getFirst()[0];
        BigDecimal topSum = (BigDecimal) rows.getFirst()[1];

        assertEquals(driver2.getId(), topDriver.getId());
        assertEquals(0, topSum.compareTo(new BigDecimal("400.00")));
    }

    @Test
    void whenFindDriverTransportStats_thenCountsAllTransportsPerDriverOrderedDesc() {
        TransportCompany company = createCompany("Acme Logistics");
        Client client = createClient(company, "Client A");
        Vehicle vehicle = createVehicle(company, "CA1234AB");

        Employee driver1 = createDriver(company, "Ivan", "Ivanov");
        Employee driver2 = createDriver(company, "Petar", "Petrov");

        // driver1: 2 transports
        service.createTransport(buildTransportRequest(company, client, vehicle, driver1,
                LocalDateTime.of(2020, 1, 10, 10, 0),
                LocalDateTime.of(2020, 1, 10, 18, 0),
                new BigDecimal("10.00"),
                true
        ));
        service.createTransport(buildTransportRequest(company, client, vehicle, driver1,
                LocalDateTime.of(2020, 2, 10, 10, 0),
                LocalDateTime.of(2020, 2, 10, 18, 0),
                new BigDecimal("20.00"),
                false
        ));

        // driver2: 1 transport
        service.createTransport(buildTransportRequest(company, client, vehicle, driver2,
                LocalDateTime.of(2020, 3, 10, 10, 0),
                LocalDateTime.of(2020, 3, 10, 18, 0),
                new BigDecimal("30.00"),
                true
        ));

        List<Object[]> rows = service.findDriverTransportStats();
        assertEquals(2, rows.size());

        Employee topDriver = (Employee) rows.getFirst()[0];
        Long topCount = (Long) rows.getFirst()[1];

        assertEquals(driver1.getId(), topDriver.getId());
        assertEquals(2L, topCount);
    }

    @Test
    void whenListCompaniesByRevenue_thenSortedByRevenueDesc() {
        TransportCompany c1 = createCompany("Company 1");
        TransportCompany c2 = createCompany("Company 2");

        Client client1 = createClient(c1, "Client A");
        Client client2 = createClient(c2, "Client B");

        Vehicle v1 = createVehicle(c1, "C1000AA");
        Vehicle v2 = createVehicle(c2, "C2000BB");

        Employee d1 = createDriver(c1, "Ivan", "Ivanov");
        Employee d2 = createDriver(c2, "Petar", "Petrov");

        // Company 1 total = 100
        service.createTransport(buildTransportRequest(c1, client1, v1, d1,
                LocalDateTime.of(2020, 1, 10, 10, 0),
                LocalDateTime.of(2020, 1, 10, 18, 0),
                new BigDecimal("100.00"),
                false
        ));

        // Company 2 total = 400
        service.createTransport(buildTransportRequest(c2, client2, v2, d2,
                LocalDateTime.of(2020, 2, 10, 10, 0),
                LocalDateTime.of(2020, 2, 10, 18, 0),
                new BigDecimal("400.00"),
                true
        ));

        List<Object[]> rows = companyRepository.findAllWithRevenueOrderByRevenueDesc();
        assertEquals(2, rows.size());

        TransportCompany topCompany = (TransportCompany) rows.getFirst()[0];
        BigDecimal topRevenue = (BigDecimal) rows.getFirst()[1];

        assertEquals(c2.getId(), topCompany.getId());
        assertEquals(0, topRevenue.compareTo(new BigDecimal("400.00")));
    }

    @Test
    void whenFindAll_thenReturnAllTransportsWithJoins() {
        TransportCompany company = createCompany("AllCo");
        Client client = createClient(company, "Client A");
        Vehicle vehicle = createVehicle(company, "T1000AA");
        Employee driver = createDriver(company, "John", "Doe");

        service.createTransport(buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2021, 1, 1, 8, 0),
                LocalDateTime.of(2021, 1, 1, 12, 0),
                new BigDecimal("50.00"),
                true
        ));

        service.createTransport(buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2021, 1, 2, 8, 0),
                LocalDateTime.of(2021, 1, 2, 12, 0),
                new BigDecimal("75.00"),
                false
        ));

        List<Transport> all = service.findAll();
        assertEquals(2, all.size());

        // Ensure joins were fetched (company, client, vehicle, driver)
        for (Transport t : all) {
            assertNotNull(t.getCompany());
            assertNotNull(t.getClient());
            assertNotNull(t.getVehicle());
            assertNotNull(t.getDriver());
        }
    }

    @Test
    void whenFindAllOrderByToLocation_thenOrderedCorrectly() {
        TransportCompany company = createCompany("OrderToCo");
        Client client = createClient(company, "Client A");
        Vehicle vehicle = createVehicle(company, "T2000BB");
        Employee driver = createDriver(company, "Alice", "Smith");

        // create transports with different to/from and departure times
        service.createTransport(buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2021, 5, 1, 9, 0),
                LocalDateTime.of(2021, 5, 1, 12, 0),
                new BigDecimal("10.00"),
                true
        ));

        // different toLocation which should come later alphabetically
        TransportCreateRequest t2 = buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2021, 4, 1, 9, 0),
                LocalDateTime.of(2021, 4, 1, 12, 0),
                new BigDecimal("20.00"),
                true
        );
        t2.setToLocation("Ztown");
        service.createTransport(t2);

        // and one with toLocation starting with A
        TransportCreateRequest t3 = buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2021, 3, 1, 9, 0),
                LocalDateTime.of(2021, 3, 1, 12, 0),
                new BigDecimal("30.00"),
                true
        );
        t3.setToLocation("AlfaCity");
        service.createTransport(t3);

        List<Transport> rows = service.findAllOrderByToLocation();
        assertEquals(3, rows.size());

        // Expect ordering by toLocation asc: AlfaCity, (original), Ztown
        assertTrue(rows.get(0).getToLocation().startsWith("AlfaCity"));
        assertEquals("Ztown", rows.get(2).getToLocation());
    }

    @Test
    void whenFindByToLocation_thenReturnOnlyMatchesAndTrimmed() {
        TransportCompany company = createCompany("ToCo");
        Client client = createClient(company, "Client A");
        Vehicle vehicle = createVehicle(company, "T3000CC");
        Employee driver = createDriver(company, "Bob", "Brown");

        TransportCreateRequest a = buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2022, 1, 1, 8, 0),
                LocalDateTime.of(2022, 1, 1, 12, 0),
                new BigDecimal("11.00"),
                true
        );
        a.setToLocation("Plovdiv");
        service.createTransport(a);

        TransportCreateRequest b = buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2022, 1, 2, 8, 0),
                LocalDateTime.of(2022, 1, 2, 12, 0),
                new BigDecimal("22.00"),
                true
        );
        b.setToLocation("  Plovdiv  "); // with surrounding whitespace
        service.createTransport(b);

        TransportCreateRequest c = buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2022, 1, 3, 8, 0),
                LocalDateTime.of(2022, 1, 3, 12, 0),
                new BigDecimal("33.00"),
                true
        );
        c.setToLocation("Sofia");
        service.createTransport(c);

        List<Transport> results = service.findByToLocation("Plovdiv");
        assertEquals(2, results.size());

        // Ensure ordered by departureDateTime ascending per repository
        assertTrue(results.get(0).getDepartureDateTime().isBefore(results.get(1).getDepartureDateTime())
                || results.get(0).getDepartureDateTime().isEqual(results.get(1).getDepartureDateTime()));

        // Trim behavior: searching with whitespace should also match
        List<Transport> resultsTrim = service.findByToLocation("  Plovdiv  ");
        assertEquals(2, resultsTrim.size());
    }

    @Test
    void whenCountAllTransports_thenReturnCorrectValue() {
        TransportCompany company = createCompany("CountCo");
        Client client = createClient(company, "Client A");
        Vehicle vehicle = createVehicle(company, "T4000DD");
        Employee driver = createDriver(company, "Eve", "Adams");

        assertEquals(0L, service.countAllTransports());

        service.createTransport(buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2023, 1, 1, 8, 0),
                LocalDateTime.of(2023, 1, 1, 12, 0),
                new BigDecimal("5.00"),
                true
        ));

        service.createTransport(buildTransportRequest(company, client, vehicle, driver,
                LocalDateTime.of(2023, 1, 2, 8, 0),
                LocalDateTime.of(2023, 1, 2, 12, 0),
                new BigDecimal("7.00"),
                false
        ));

        assertEquals(2L, service.countAllTransports());
    }

    private TransportCompany createCompany(String name) {
        TransportCompany company = new TransportCompany();
        company.setName(name);
        return companyRepository.create(company);
    }

    private Client createClient(TransportCompany company, String name) {
        Client client = new Client();
        client.setName(name);
        client.setCompany(company);
        return clientRepository.create(client);
    }

    private Vehicle createVehicle(TransportCompany company, String regNumber) {
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber(regNumber);
        vehicle.setType(VehicleType.TRUCK);
        vehicle.setCapacity(1000);
        vehicle.setCompany(company);
        return vehicleRepository.create(vehicle);
    }

    private Employee createDriver(TransportCompany company, String firstName, String lastName) {
        Employee driver = new Employee();
        driver.setFirstName(firstName);
        driver.setLastName(lastName);
        driver.setQualification(EmployeeQualification.DRIVER_STANDARD);
        driver.setSalary(new BigDecimal("2000.00"));
        driver.setCompany(company);
        return employeeRepository.create(driver);
    }

    private TransportCreateRequest buildTransportRequest(
            TransportCompany company,
            Client client,
            Vehicle vehicle,
            Employee driver,
            LocalDateTime departure,
            LocalDateTime arrival,
            BigDecimal price,
            boolean paid
    ) {
        TransportCreateRequest request = new TransportCreateRequest();
        request.setCompanyId(company.getId());
        request.setClientId(client.getId());
        request.setVehicleId(vehicle.getId());
        request.setDriverId(driver.getId());
        request.setFromLocation("Sofia");
        request.setToLocation("Plovdiv");
        request.setDeparture(departure);
        request.setArrival(arrival);
        request.setCargoDescription("cargo");
        request.setCargoWeight(1.0);
        request.setPrice(price);
        request.setPaid(paid);
        return request;
    }
}
