package org.informatics.transportcompany.service;

import org.hibernate.SessionFactory;
import org.informatics.transportcompany.config.HibernateUtil;
import org.informatics.transportcompany.exceptions.client.NoClientWithProvidedIdException;
import org.informatics.transportcompany.exceptions.company.NoCompanyWithProvidedIdException;
import org.informatics.transportcompany.model.dto.client.ClientCreateRequest;
import org.informatics.transportcompany.model.dto.client.ClientUpdateRequest;
import org.informatics.transportcompany.model.entity.Client;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.repository.client.ClientRepository;
import org.informatics.transportcompany.repository.client.ClientRepositoryImpl;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientServiceTest {

    private static SessionFactory sessionFactory;

    private TransportCompanyRepository companyRepository;

    private ClientService service;

    @BeforeAll
    static void setup() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @BeforeEach
    void initTests() {
        sessionFactory.getSchemaManager().truncateMappedObjects();

        companyRepository = new TransportCompanyRepositoryImpl();
        ClientRepository clientRepository = new ClientRepositoryImpl();
        service = new ClientService(clientRepository, companyRepository);
    }

    @AfterEach
    void endTests() {
        sessionFactory.getSchemaManager().truncateMappedObjects();
    }

    @Test
    void whenCompanyMissing_thenThrowNoCompanyWithProvidedIdException() {
        ClientCreateRequest request = new ClientCreateRequest();
        request.setCompanyId(999);
        request.setName("Client A");
        request.setContactDetails("contact");

        assertThrows(NoCompanyWithProvidedIdException.class, () -> service.createClient(request));
    }

    @Test
    void givenCompany_whenCreateClient_thenClientIsPersistedWithCompany() {
        TransportCompany company = new TransportCompany();
        company.setName("Acme Logistics");
        companyRepository.create(company);

        ClientCreateRequest request = new ClientCreateRequest();
        request.setCompanyId(company.getId());
        request.setName("Client A");
        request.setContactDetails("+359 888 123 456");

        Client created = service.createClient(request);

        assertNotNull(created.getId());

        List<Client> clients = service.findAll();
        assertEquals(1, clients.size());
        assertEquals("Client A", clients.getFirst().getName());
        assertEquals("Acme Logistics", clients.getFirst().getCompany().getName());
    }

    @Test
    void whenFindByIdExists_thenReturnClient() {
        TransportCompany company = new TransportCompany();
        company.setName("FindClientCo");
        companyRepository.create(company);

        ClientCreateRequest request = new ClientCreateRequest();
        request.setCompanyId(company.getId());
        request.setName("Client B");
        request.setContactDetails("contactB");

        Client created = service.createClient(request);

        Client found = service.findById(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Client B", found.getName());
    }

    @Test
    void whenFindByIdMissing_thenThrowNoClientWithProvidedIdException() {
        assertThrows(NoClientWithProvidedIdException.class, () -> service.findById(99999));
    }

    @Test
    void whenUpdateClient_thenClientUpdated() {
        TransportCompany company = new TransportCompany();
        company.setName("UpdateClientCo");
        companyRepository.create(company);

        ClientCreateRequest createReq = new ClientCreateRequest();
        createReq.setCompanyId(company.getId());
        createReq.setName("Client C");
        createReq.setContactDetails("contactC");

        Client created = service.createClient(createReq);

        ClientUpdateRequest updateReq = new ClientUpdateRequest();
        updateReq.setId(created.getId());
        updateReq.setName("Client C Updated");
        updateReq.setContactDetails("newContact");

        Client updated = service.updateClient(updateReq);

        assertEquals(created.getId(), updated.getId());
        assertEquals("Client C Updated", updated.getName());
        assertEquals("newContact", updated.getContactDetails());
    }

    @Test
    void whenUpdateMissing_thenThrowNoClientWithProvidedIdException() {
        ClientUpdateRequest updateReq = new ClientUpdateRequest();
        updateReq.setId(88888L);
        updateReq.setName("X");
        updateReq.setContactDetails("Y");

        assertThrows(NoClientWithProvidedIdException.class, () -> service.updateClient(updateReq));
    }

    @Test
    void whenDeleteClient_thenRemoved() {
        TransportCompany company = new TransportCompany();
        company.setName("DeleteClientCo");
        companyRepository.create(company);

        ClientCreateRequest createReq = new ClientCreateRequest();
        createReq.setCompanyId(company.getId());
        createReq.setName("Client D");
        createReq.setContactDetails("contactD");

        Client created = service.createClient(createReq);

        // ensure present
        assertNotNull(service.findById(created.getId()));
        assertEquals(1, service.findAll().size());

        service.deleteClient(created.getId());

        assertEquals(0, service.findAll().size());
        assertThrows(NoClientWithProvidedIdException.class, () -> service.findById(created.getId()));
    }

    @Test
    void whenDeleteNonExisting_thenNoException() {
        // deleting non-existing client should not throw
        service.deleteClient(777777);
    }
}
