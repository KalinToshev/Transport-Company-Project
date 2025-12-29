package org.informatics.transportcompany.repository.client;

import org.informatics.transportcompany.model.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    Client create(Client client);

    Client update(Client client);

    Optional<Client> findById(long id);

    List<Client> findAll();

    void deleteById(long id);
}
