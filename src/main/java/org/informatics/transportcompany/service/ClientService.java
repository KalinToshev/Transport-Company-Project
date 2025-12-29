package org.informatics.transportcompany.service;

import lombok.RequiredArgsConstructor;
import org.informatics.transportcompany.exceptions.client.NoClientWithProvidedIdException;
import org.informatics.transportcompany.exceptions.company.NoCompanyWithProvidedIdException;
import org.informatics.transportcompany.model.dto.client.ClientCreateRequest;
import org.informatics.transportcompany.model.dto.client.ClientUpdateRequest;
import org.informatics.transportcompany.model.entity.Client;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.repository.client.ClientRepository;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;

import java.util.List;

@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final TransportCompanyRepository transportCompanyRepository;

    public Client createClient(ClientCreateRequest request) {
        TransportCompany company = transportCompanyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new NoCompanyWithProvidedIdException("No company with id = " + request.getCompanyId()));

        Client client = new Client();
        client.setName(request.getName());
        client.setContactDetails(request.getContactDetails());
        client.setCompany(company);

        return clientRepository.create(client);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NoClientWithProvidedIdException("No client with id = " + id));
    }

    public Client updateClient(ClientUpdateRequest request) {
        Client client = clientRepository.findById(request.getId())
                .orElseThrow(() -> new NoClientWithProvidedIdException("No client with id = " + request.getId()));

        client.setName(request.getName());
        client.setContactDetails(request.getContactDetails());

        return clientRepository.update(client);
    }

    public void deleteClient(long id) {
        clientRepository.deleteById(id);
    }
}