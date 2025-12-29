package org.informatics.transportcompany.repository;

import jakarta.validation.ConstraintViolationException;
import org.hibernate.SessionFactory;
import org.informatics.transportcompany.config.HibernateUtil;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransportCompanyRepositoryTest {

    private static SessionFactory sessionFactory;
    private TransportCompanyRepository repository;

    @BeforeAll
    static void setup() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @BeforeEach
    void initTests() {
        repository = new TransportCompanyRepositoryImpl();
        sessionFactory.getSchemaManager().truncateMappedObjects();
    }

    @AfterEach
    void endTests() {
        sessionFactory.getSchemaManager().truncateMappedObjects();
    }

    @Test
    void givenCompany_whenCreate_thenFindAllReturnsIt() {
        TransportCompany company = new TransportCompany();
        company.setName("Acme Logistics");
        company.setAddress("Sofia");

        repository.create(company);

        List<TransportCompany> companies = repository.findAll();
        assertEquals(1, companies.size());
        assertEquals("Acme Logistics", companies.getFirst().getName());
        assertEquals("Sofia", companies.getFirst().getAddress());
    }

    @Test
    void whenNameBlank_thenConstraintViolationEx() {
        TransportCompany company = new TransportCompany();
        company.setName("  ");

        assertThrows(ConstraintViolationException.class, () -> repository.create(company));
    }

    @Test
    void givenCompanies_whenFindAllOrderByName_thenSortedAsc() {
        TransportCompany c1 = new TransportCompany();
        c1.setName("Beta");

        TransportCompany c2 = new TransportCompany();
        c2.setName("Alpha");

        TransportCompany c3 = new TransportCompany();
        c3.setName("Gamma");

        repository.create(c1);
        repository.create(c2);
        repository.create(c3);

        List<TransportCompany> companies = repository.findAllOrderByName();
        assertEquals(List.of("Alpha", "Beta", "Gamma"), companies.stream().map(TransportCompany::getName).toList());
    }
}
