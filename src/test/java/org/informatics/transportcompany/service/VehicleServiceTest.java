package org.informatics.transportcompany.service;

import org.hibernate.SessionFactory;
import org.informatics.transportcompany.config.HibernateUtil;
import org.informatics.transportcompany.exceptions.company.NoCompanyWithProvidedIdException;
import org.informatics.transportcompany.exceptions.vehicle.NoVehicleWithProvidedIdException;
import org.informatics.transportcompany.model.dto.vehicle.VehicleCreateRequest;
import org.informatics.transportcompany.model.dto.vehicle.VehicleUpdateRequest;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.model.entity.Vehicle;
import org.informatics.transportcompany.model.enums.VehicleType;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepositoryImpl;
import org.informatics.transportcompany.repository.vehicle.VehicleRepository;
import org.informatics.transportcompany.repository.vehicle.VehicleRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VehicleServiceTest {

    private static SessionFactory sessionFactory;

    private TransportCompanyRepository companyRepository;

    private VehicleService service;

    @BeforeAll
    static void setup() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @BeforeEach
    void initTests() {
        sessionFactory.getSchemaManager().truncateMappedObjects();

        companyRepository = new TransportCompanyRepositoryImpl();
        VehicleRepository vehicleRepository = new VehicleRepositoryImpl();
        service = new VehicleService(vehicleRepository, companyRepository);
    }

    @AfterEach
    void endTests() {
        sessionFactory.getSchemaManager().truncateMappedObjects();
    }

    @Test
    void whenCompanyMissing_thenThrowNoCompanyWithProvidedIdException() {
        VehicleCreateRequest request = new VehicleCreateRequest();
        request.setCompanyId(999);
        request.setRegistrationNumber("CA1234AB");
        request.setType(VehicleType.TRUCK);
        request.setCapacity(1000);

        assertThrows(NoCompanyWithProvidedIdException.class, () -> service.createVehicle(request));
    }

    @Test
    void givenCompany_whenCreateVehicle_thenVehicleIsPersistedWithCompany() {
        TransportCompany company = new TransportCompany();
        company.setName("Acme Logistics");
        companyRepository.create(company);

        VehicleCreateRequest request = new VehicleCreateRequest();
        request.setCompanyId(company.getId());
        request.setRegistrationNumber("CA1234AB");
        request.setType(VehicleType.TRUCK);
        request.setCapacity(1000);

        Vehicle created = service.createVehicle(request);

        assertNotNull(created.getId());
        assertEquals("CA1234AB", created.getRegistrationNumber());
        assertEquals("Acme Logistics", created.getCompany().getName());
    }

    @Test
    void whenFindAll_thenReturnAllVehicles() {
        TransportCompany company = new TransportCompany();
        company.setName("FleetCo");
        companyRepository.create(company);

        VehicleCreateRequest r1 = new VehicleCreateRequest();
        r1.setCompanyId(company.getId());
        r1.setRegistrationNumber("CA1111AB");
        r1.setType(VehicleType.TRUCK);
        r1.setCapacity(1000);
        service.createVehicle(r1);

        VehicleCreateRequest r2 = new VehicleCreateRequest();
        r2.setCompanyId(company.getId());
        r2.setRegistrationNumber("CA2222AB");
        r2.setType(VehicleType.CAR);
        r2.setCapacity(5);
        service.createVehicle(r2);

        List<Vehicle> all = service.findAll();
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(v -> "CA1111AB".equals(v.getRegistrationNumber())));
        assertTrue(all.stream().anyMatch(v -> "CA2222AB".equals(v.getRegistrationNumber())));
    }

    @Test
    void whenFindAllEmpty_thenReturnEmptyList() {
        List<Vehicle> all = service.findAll();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void whenFindAll_thenResultsOrderedById() {
        TransportCompany company = new TransportCompany();
        company.setName("OrderCo");
        companyRepository.create(company);

        // create multiple vehicles; created IDs should be ascending
        VehicleCreateRequest a = new VehicleCreateRequest();
        a.setCompanyId(company.getId());
        a.setRegistrationNumber("A1");
        a.setType(VehicleType.CAR);
        a.setCapacity(4);
        service.createVehicle(a);

        VehicleCreateRequest b = new VehicleCreateRequest();
        b.setCompanyId(company.getId());
        b.setRegistrationNumber("B2");
        b.setType(VehicleType.VAN);
        b.setCapacity(200);
        service.createVehicle(b);

        List<Vehicle> all = service.findAll();
        assertEquals(2, all.size());
        assertTrue(all.get(0).getId() < all.get(1).getId(), "Results should be ordered by id ascending");

        // sanity: ensure company was fetched for each vehicle
        assertNotNull(all.get(0).getCompany());
        assertNotNull(all.get(1).getCompany());
    }

    @Test
    void whenFindByIdExists_thenReturnVehicle() {
        TransportCompany company = new TransportCompany();
        company.setName("FindCo");
        companyRepository.create(company);

        VehicleCreateRequest request = new VehicleCreateRequest();
        request.setCompanyId(company.getId());
        request.setRegistrationNumber("CA3333AB");
        request.setType(VehicleType.VAN);
        request.setCapacity(500);

        Vehicle created = service.createVehicle(request);

        Vehicle found = service.findById(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("CA3333AB", found.getRegistrationNumber());
    }

    @Test
    void whenFindByIdMissing_thenReturnNull() {
        assertNull(service.findById(9999));
    }

    @Test
    void whenUpdateVehicle_thenVehicleUpdated() {
        TransportCompany company = new TransportCompany();
        company.setName("UpdateCo");
        companyRepository.create(company);

        VehicleCreateRequest createReq = new VehicleCreateRequest();
        createReq.setCompanyId(company.getId());
        createReq.setRegistrationNumber("CA4444AB");
        createReq.setType(VehicleType.TRUCK);
        createReq.setCapacity(800);

        Vehicle created = service.createVehicle(createReq);

        VehicleUpdateRequest updateReq = new VehicleUpdateRequest();
        updateReq.setId(created.getId());
        updateReq.setRegistrationNumber("CA4444XY");
        updateReq.setType(VehicleType.CAR);
        updateReq.setCapacity(4);

        Vehicle updated = service.updateVehicle(updateReq);

        assertEquals(created.getId(), updated.getId());
        assertEquals("CA4444XY", updated.getRegistrationNumber());
        assertEquals(VehicleType.CAR, updated.getType());
        assertEquals(4, updated.getCapacity());
    }

    @Test
    void whenUpdateVehicle_thenCompanyRemainsUnchanged() {
        TransportCompany company = new TransportCompany();
        company.setName("KeepCo");
        companyRepository.create(company);

        VehicleCreateRequest createReq = new VehicleCreateRequest();
        createReq.setCompanyId(company.getId());
        createReq.setRegistrationNumber("KEEP1");
        createReq.setType(VehicleType.VAN);
        createReq.setCapacity(100);

        Vehicle created = service.createVehicle(createReq);

        VehicleUpdateRequest updateReq = new VehicleUpdateRequest();
        updateReq.setId(created.getId());
        updateReq.setRegistrationNumber("KEEP2");
        updateReq.setType(VehicleType.CAR);
        updateReq.setCapacity(2);

        Vehicle updated = service.updateVehicle(updateReq);

        assertNotNull(updated.getCompany());
        assertEquals(company.getId(), updated.getCompany().getId());
        assertEquals("KeepCo", updated.getCompany().getName());
    }

    @Test
    void whenUpdateMissing_thenThrowNoVehicleWithProvidedIdException() {
        VehicleUpdateRequest req = new VehicleUpdateRequest();
        req.setId(99999);
        req.setRegistrationNumber("ZZZ");
        req.setType(VehicleType.CAR);
        req.setCapacity(1);

        assertThrows(NoVehicleWithProvidedIdException.class, () -> service.updateVehicle(req));
    }

    @Test
    void whenDeleteVehicle_thenRemoved() {
        TransportCompany company = new TransportCompany();
        company.setName("DeleteCo");
        companyRepository.create(company);

        VehicleCreateRequest createReq = new VehicleCreateRequest();
        createReq.setCompanyId(company.getId());
        createReq.setRegistrationNumber("CA5555AB");
        createReq.setType(VehicleType.VAN);
        createReq.setCapacity(300);

        Vehicle created = service.createVehicle(createReq);

        // ensure present
        assertNotNull(service.findById(created.getId()));
        assertEquals(1, service.findAll().size());

        service.deleteVehicle(created.getId());

        assertNull(service.findById(created.getId()));
        assertEquals(0, service.findAll().size());
    }

    @Test
    void whenDeleteNonExisting_thenNoExceptionAndNoChange() {
        TransportCompany company = new TransportCompany();
        company.setName("NoopCo");
        companyRepository.create(company);

        VehicleCreateRequest createReq = new VehicleCreateRequest();
        createReq.setCompanyId(company.getId());
        createReq.setRegistrationNumber("KEEP");
        createReq.setType(VehicleType.CAR);
        createReq.setCapacity(4);

        service.createVehicle(createReq);

        // deleting a non-existing id should not throw
        service.deleteVehicle(999999);

        // original vehicle still present
        List<Vehicle> all = service.findAll();
        assertEquals(1, all.size());
    }

    @Test
    void whenDeleteOneOfMultiple_thenOnlyThatOneRemoved() {
        TransportCompany company = new TransportCompany();
        company.setName("MultiDelCo");
        companyRepository.create(company);

        VehicleCreateRequest a = new VehicleCreateRequest();
        a.setCompanyId(company.getId());
        a.setRegistrationNumber("D1");
        a.setType(VehicleType.TRUCK);
        a.setCapacity(1000);
        Vehicle v1 = service.createVehicle(a);

        VehicleCreateRequest b = new VehicleCreateRequest();
        b.setCompanyId(company.getId());
        b.setRegistrationNumber("D2");
        b.setType(VehicleType.VAN);
        b.setCapacity(200);
        Vehicle v2 = service.createVehicle(b);

        assertEquals(2, service.findAll().size());

        service.deleteVehicle(v1.getId());

        List<Vehicle> remaining = service.findAll();
        assertEquals(1, remaining.size());
        assertEquals(v2.getId(), remaining.getFirst().getId());
    }
}
