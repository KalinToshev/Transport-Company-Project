package org.informatics.transportcompany.service;

import org.hibernate.SessionFactory;
import org.informatics.transportcompany.config.HibernateUtil;
import org.informatics.transportcompany.exceptions.company.NoCompanyWithProvidedIdException;
import org.informatics.transportcompany.model.dto.transportCompany.TransportCompanyCreateRequest;
import org.informatics.transportcompany.model.dto.transportCompany.TransportCompanyUpdateRequest;
import org.informatics.transportcompany.model.entity.Client;
import org.informatics.transportcompany.model.entity.Employee;
import org.informatics.transportcompany.model.entity.Transport;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.model.entity.Vehicle;
import org.informatics.transportcompany.model.enums.EmployeeQualification;
import org.informatics.transportcompany.model.enums.VehicleType;
import org.informatics.transportcompany.repository.client.ClientRepositoryImpl;
import org.informatics.transportcompany.repository.employee.EmployeeRepositoryImpl;
import org.informatics.transportcompany.repository.transport.TransportRepositoryImpl;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepositoryImpl;
import org.informatics.transportcompany.repository.vehicle.VehicleRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransportCompanyServiceTest {

    private static SessionFactory sessionFactory;

    private TransportCompanyRepository companyRepository;

    private ClientRepositoryImpl clientRepository;
    private VehicleRepositoryImpl vehicleRepository;
    private EmployeeRepositoryImpl employeeRepository;
    private TransportRepositoryImpl transportRepository;

    private TransportCompanyService service;

    @BeforeAll
    static void setup() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @BeforeEach
    void init() {
        sessionFactory.getSchemaManager().truncateMappedObjects();

        companyRepository = new TransportCompanyRepositoryImpl();
        clientRepository = new ClientRepositoryImpl();
        vehicleRepository = new VehicleRepositoryImpl();
        employeeRepository = new EmployeeRepositoryImpl();
        transportRepository = new TransportRepositoryImpl();

        service = new TransportCompanyService(companyRepository);
    }

    @AfterEach
    void tearDown() {
        sessionFactory.getSchemaManager().truncateMappedObjects();
    }

    @Test
    void whenCreateCompany_thenPersisted() {
        TransportCompanyCreateRequest req = new TransportCompanyCreateRequest();
        req.setName("NewCo");
        req.setAddress("Some address");

        TransportCompany created = service.createCompany(req);
        assertNotNull(created.getId());
        assertEquals("NewCo", created.getName());
    }

    @Test
    void whenFindAllEmpty_thenReturnEmptyList() {
        List<TransportCompany> all = service.findAll();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void whenFindAllOrderByName_thenReturnOrdered() {
        TransportCompany a = new TransportCompany();
        a.setName("Zeta");
        companyRepository.create(a);

        TransportCompany b = new TransportCompany();
        b.setName("Alpha");
        companyRepository.create(b);

        List<TransportCompany> rows = service.findAllOrderByName();
        assertEquals(2, rows.size());
        assertEquals("Alpha", rows.get(0).getName());
        assertEquals("Zeta", rows.get(1).getName());
    }

    @Test
    void whenFindAllWithRevenueOrderByRevenueDesc_thenReturnCompaniesWithSum() {
        // Create two companies
        TransportCompany c1 = new TransportCompany();
        c1.setName("Company One");
        companyRepository.create(c1);

        TransportCompany c2 = new TransportCompany();
        c2.setName("Company Two");
        companyRepository.create(c2);

        // For each company create required related entities and transports
        Client client1 = new Client();
        client1.setName("Client1");
        client1.setCompany(c1);
        clientRepository.create(client1);

        Vehicle v1 = new Vehicle();
        v1.setRegistrationNumber("REG1");
        v1.setType(VehicleType.TRUCK);
        v1.setCapacity(1);
        v1.setCompany(c1);
        vehicleRepository.create(v1);

        Employee d1 = new Employee();
        d1.setFirstName("D1");
        d1.setLastName("One");
        d1.setQualification(EmployeeQualification.DRIVER_STANDARD);
        d1.setSalary(new BigDecimal("1000"));
        d1.setCompany(c1);
        employeeRepository.create(d1);

        Transport t1 = new Transport();
        t1.setCompany(c1);
        t1.setClient(client1);
        t1.setVehicle(v1);
        t1.setDriver(d1);
        t1.setFromLocation("Sofia");
        t1.setToLocation("Plovdiv");
        t1.setDepartureDateTime(LocalDateTime.now().minusDays(2));
        t1.setArrivalDateTime(LocalDateTime.now().minusDays(2));
        t1.setCargoDescription("x");
        t1.setCargoWeight(1.0);
        t1.setPrice(new BigDecimal("100.00"));
        t1.setPaid(true);
        transportRepository.create(t1);

        // Company 2 with higher revenue
        Client client2 = new Client();
        client2.setName("Client2");
        client2.setCompany(c2);
        clientRepository.create(client2);

        Vehicle v2 = new Vehicle();
        v2.setRegistrationNumber("REG2");
        v2.setType(VehicleType.VAN);
        v2.setCapacity(1);
        v2.setCompany(c2);
        vehicleRepository.create(v2);

        Employee d2 = new Employee();
        d2.setFirstName("D2");
        d2.setLastName("Two");
        d2.setQualification(EmployeeQualification.DRIVER_STANDARD);
        d2.setSalary(new BigDecimal("2000"));
        d2.setCompany(c2);
        employeeRepository.create(d2);

        Transport t2 = new Transport();
        t2.setCompany(c2);
        t2.setClient(client2);
        t2.setVehicle(v2);
        t2.setDriver(d2);
        t2.setFromLocation("Sofia");
        t2.setToLocation("Varna");
        t2.setDepartureDateTime(LocalDateTime.now().minusDays(1));
        t2.setArrivalDateTime(LocalDateTime.now().minusDays(1));
        t2.setCargoDescription("y");
        t2.setCargoWeight(1.0);
        t2.setPrice(new BigDecimal("300.00"));
        t2.setPaid(true);
        transportRepository.create(t2);

        List<Object[]> rows = service.findAllWithRevenueOrderByRevenueDesc();
        assertEquals(2, rows.size());

        TransportCompany top = (TransportCompany) rows.getFirst()[0];
        BigDecimal topRevenue = (BigDecimal) rows.getFirst()[1];

        assertEquals(c2.getId(), top.getId());
        assertEquals(0, topRevenue.compareTo(new BigDecimal("300.00")));
    }

    @Test
    void whenFindByIdExists_thenReturnCompany() {
        TransportCompany c = new TransportCompany();
        c.setName("FindCo");
        companyRepository.create(c);

        TransportCompany found = service.findById(c.getId());
        assertNotNull(found);
        assertEquals(c.getId(), found.getId());
    }

    @Test
    void whenFindByIdMissing_thenThrowNoCompanyWithProvidedIdException() {
        assertThrows(NoCompanyWithProvidedIdException.class, () -> service.findById(99999L));
    }

    @Test
    void whenUpdateCompany_thenUpdated() {
        TransportCompany c = new TransportCompany();
        c.setName("UpdCo");
        c.setAddress("addr");
        companyRepository.create(c);

        TransportCompanyUpdateRequest req = new TransportCompanyUpdateRequest();
        req.setId(c.getId());
        req.setName("UpdCoNew");
        req.setAddress("new addr");

        TransportCompany updated = service.updateCompany(req);
        assertEquals(c.getId(), updated.getId());
        assertEquals("UpdCoNew", updated.getName());
        assertEquals("new addr", updated.getAddress());
    }

    @Test
    void whenUpdateMissing_thenThrowNoCompanyWithProvidedIdException() {
        TransportCompanyUpdateRequest req = new TransportCompanyUpdateRequest();
        req.setId(55555L);
        req.setName("X");
        req.setAddress("Y");

        assertThrows(NoCompanyWithProvidedIdException.class, () -> service.updateCompany(req));
    }

    @Test
    void whenDeleteCompany_thenRemoved() {
        TransportCompany c = new TransportCompany();
        c.setName("DelCo");
        companyRepository.create(c);

        assertNotNull(service.findById(c.getId()));

        service.deleteCompany(c.getId());

        assertThrows(NoCompanyWithProvidedIdException.class, () -> service.findById(c.getId()));
    }
}
