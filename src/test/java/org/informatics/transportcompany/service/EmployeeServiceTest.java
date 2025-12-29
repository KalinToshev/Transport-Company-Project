package org.informatics.transportcompany.service;

import org.hibernate.SessionFactory;
import org.informatics.transportcompany.config.HibernateUtil;
import org.informatics.transportcompany.exceptions.company.NoCompanyWithProvidedIdException;
import org.informatics.transportcompany.exceptions.employee.NoEmployeeWithProvidedIdException;
import org.informatics.transportcompany.model.dto.employee.EmployeeCreateRequest;
import org.informatics.transportcompany.model.dto.employee.EmployeeUpdateRequest;
import org.informatics.transportcompany.model.entity.Employee;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.model.enums.EmployeeQualification;
import org.informatics.transportcompany.repository.employee.EmployeeRepository;
import org.informatics.transportcompany.repository.employee.EmployeeRepositoryImpl;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    private static SessionFactory sessionFactory;

    private TransportCompanyRepository companyRepository;
    private EmployeeRepository employeeRepository;

    private EmployeeService service;

    @BeforeAll
    static void setup() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @BeforeEach
    void initTests() {
        sessionFactory.getSchemaManager().truncateMappedObjects();

        companyRepository = new TransportCompanyRepositoryImpl();
        employeeRepository = new EmployeeRepositoryImpl();
        service = new EmployeeService(employeeRepository, companyRepository);
    }

    @AfterEach
    void tearDown() {
        sessionFactory.getSchemaManager().truncateMappedObjects();
    }

    @Test
    void whenCompanyMissing_thenThrowNoCompanyWithProvidedIdException() {
        EmployeeCreateRequest req = new EmployeeCreateRequest();
        req.setCompanyId(999);
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setQualification(EmployeeQualification.DRIVER_STANDARD);
        req.setSalary(new BigDecimal("1000.00"));

        assertThrows(NoCompanyWithProvidedIdException.class, () -> service.createEmployee(req));
    }

    @Test
    void givenCompany_whenCreateEmployee_thenEmployeePersistedWithCompany() {
        TransportCompany company = new TransportCompany();
        company.setName("EmpCo");
        companyRepository.create(company);

        EmployeeCreateRequest req = new EmployeeCreateRequest();
        req.setCompanyId(company.getId());
        req.setFirstName("Jane");
        req.setLastName("Roe");
        req.setQualification(EmployeeQualification.DRIVER_STANDARD);
        req.setSalary(new BigDecimal("1500.00"));

        Employee created = service.createEmployee(req);

        assertNotNull(created.getId());
        assertEquals("Jane", created.getFirstName());
        assertEquals(company.getId(), created.getCompany().getId());
    }

    @Test
    void whenFindAll_thenReturnAllEmployees() {
        TransportCompany company = new TransportCompany();
        company.setName("AllEmpCo");
        companyRepository.create(company);

        createEmployee(company, "A", "One", EmployeeQualification.DRIVER_STANDARD, new BigDecimal("1000"));
        createEmployee(company, "B", "Two", EmployeeQualification.MECHANIC, new BigDecimal("2000"));

        List<Employee> all = service.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void whenFindByIdExists_thenReturnEmployee() {
        TransportCompany company = new TransportCompany();
        company.setName("FindEmpCo");
        companyRepository.create(company);

        Employee e = createEmployee(company, "X", "Y", EmployeeQualification.OFFICE, new BigDecimal("3000"));

        Employee found = service.findById(e.getId());
        assertNotNull(found);
        assertEquals(e.getId(), found.getId());
    }

    @Test
    void whenFindByIdMissing_thenReturnNull() {
        assertNull(service.findById(1234567L));
    }

    @Test
    void whenFindAllOrderByQualificationThenSalary_thenOrderedCorrectly() {
        TransportCompany company = new TransportCompany();
        company.setName("OrderEmpCo");
        companyRepository.create(company);

        // Create employees with different qualifications and salaries
        Employee e1 = createEmployee(company, "AA", "One", EmployeeQualification.DRIVER_STANDARD, new BigDecimal("1000"));
        Employee e2 = createEmployee(company, "BB", "Two", EmployeeQualification.DRIVER_STANDARD, new BigDecimal("2000"));
        createEmployee(company, "CC", "Three", EmployeeQualification.MECHANIC, new BigDecimal("1500"));

        List<Employee> rows = service.findAllOrderByQualificationThenSalary();
        assertEquals(3, rows.size());

        // Qualification ordering: DRIVER_STANDARD (asc), MECHANIC, MANAGER etc.
        // Within DRIVER_STANDARD, salary desc -> e2 before e1
        int idxE2 = indexOf(rows, e2.getId());
        int idxE1 = indexOf(rows, e1.getId());
        assertTrue(idxE2 < idxE1, "Driver with higher salary should come before lower salary");

        // Ensure company fetched
        for (Employee r : rows) {
            assertNotNull(r.getCompany());
        }
    }

    @Test
    void whenFindAllOrderBySalaryDesc_thenOrderedBySalary() {
        TransportCompany company = new TransportCompany();
        company.setName("SalaryEmpCo");
        companyRepository.create(company);

        Employee high = createEmployee(company, "High", "Paid", EmployeeQualification.OFFICE, new BigDecimal("5000"));
        Employee low = createEmployee(company, "Low", "Paid", EmployeeQualification.MECHANIC, new BigDecimal("1000"));

        List<Employee> rows = service.findAllOrderBySalaryDesc();
        assertEquals(2, rows.size());
        assertEquals(high.getId(), rows.get(0).getId());
        assertEquals(low.getId(), rows.get(1).getId());
    }

    @Test
    void whenFindByQualificationOrderBySalaryDesc_thenFiltersAndOrders() {
        TransportCompany company = new TransportCompany();
        company.setName("QualEmpCo");
        companyRepository.create(company);

        Employee a = createEmployee(company, "A", "One", EmployeeQualification.DRIVER_STANDARD, new BigDecimal("1000"));
        Employee b = createEmployee(company, "B", "Two", EmployeeQualification.DRIVER_STANDARD, new BigDecimal("3000"));
        createEmployee(company, "C", "Three", EmployeeQualification.MECHANIC, new BigDecimal("2000"));

        List<Employee> rows = service.findByQualificationOrderBySalaryDesc(EmployeeQualification.DRIVER_STANDARD);
        assertEquals(2, rows.size());
        assertEquals(b.getId(), rows.get(0).getId());
        assertEquals(a.getId(), rows.get(1).getId());
    }

    @Test
    void whenUpdateEmployee_thenEmployeeUpdated() {
        TransportCompany company = new TransportCompany();
        company.setName("UpdateEmpCo");
        companyRepository.create(company);

        Employee created = createEmployee(company, "Upd", "Me", EmployeeQualification.DRIVER_STANDARD, new BigDecimal("1200"));

        EmployeeUpdateRequest req = new EmployeeUpdateRequest();
        req.setId(created.getId());
        req.setFirstName("Updated");
        req.setLastName("Name");
        req.setQualification(EmployeeQualification.OFFICE);
        req.setSalary(new BigDecimal("9999"));

        Employee updated = service.updateEmployee(req);
        assertEquals(created.getId(), updated.getId());
        assertEquals("Updated", updated.getFirstName());
        assertEquals(EmployeeQualification.OFFICE, updated.getQualification());
        assertEquals(0, updated.getSalary().compareTo(new BigDecimal("9999")));
    }

    @Test
    void whenUpdateMissing_thenThrowNoEmployeeWithProvidedIdException() {
        EmployeeUpdateRequest req = new EmployeeUpdateRequest();
        req.setId(5555555);
        req.setFirstName("X");
        req.setLastName("Y");
        req.setQualification(EmployeeQualification.DRIVER_STANDARD);
        req.setSalary(new BigDecimal("1"));

        assertThrows(NoEmployeeWithProvidedIdException.class, () -> service.updateEmployee(req));
    }

    @Test
    void whenDeleteEmployee_thenRemovedAndNonExistingNoOp() {
        TransportCompany company = new TransportCompany();
        company.setName("DeleteEmpCo");
        companyRepository.create(company);

        Employee e = createEmployee(company, "Del", "One", EmployeeQualification.DRIVER_STANDARD, new BigDecimal("1100"));

        assertNotNull(service.findById(e.getId()));

        service.deleteEmployee(e.getId());
        assertNull(service.findById(e.getId()));

        // deleting non-existing should not throw
        service.deleteEmployee(99999999L);
    }

    // helper methods
    private Employee createEmployee(TransportCompany company, String firstName, String lastName, EmployeeQualification qual, BigDecimal salary) {
        Employee emp = new Employee();
        emp.setFirstName(firstName);
        emp.setLastName(lastName);
        emp.setQualification(qual);
        emp.setSalary(salary);
        emp.setCompany(company);
        return employeeRepository.create(emp);
    }

    private int indexOf(List<Employee> list, long id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) return i;
        }
        return -1;
    }
}
