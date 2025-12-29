package org.informatics.transportcompany.handlers;

import org.informatics.transportcompany.ConsoleHelper;
import org.informatics.transportcompany.model.dto.client.ClientCreateRequest;
import org.informatics.transportcompany.model.dto.client.ClientUpdateRequest;
import org.informatics.transportcompany.model.entity.Client;
import org.informatics.transportcompany.service.ClientService;

import java.util.List;

public class ClientsHandler {

    private static final ConsoleHelper consoleHelper = new ConsoleHelper();

    public static void handleCreateClient(ClientService service) {
        long companyId = consoleHelper.readLong("Company ID: ");

        String name = consoleHelper.readLine("Customer name: ");

        String contact = consoleHelper.readLine("Contact details: ");

        ClientCreateRequest request = new ClientCreateRequest();
        request.setCompanyId(companyId);
        request.setName(name);
        request.setContactDetails(contact);

        Client client = service.createClient(request);

        System.out.println("Created client with id = " + client.getId());
    }

    public static void handleListClients(ClientService service) {
        List<Client> clients = service.findAll();

        if (clients.isEmpty()) {
            System.out.println("No registered clients.");
            return;
        }

        for (Client client : clients) {
            String contact = client.getContactDetails();

            System.out.printf("[%d] %s (%s), company: %s%n",
                    client.getId(),
                    client.getName(),
                    contact == null ? "" : contact,
                    client.getCompany().getName()
            );
        }
    }

    public static void handleEditClient(ClientService service) {
        long id = consoleHelper.readLong("Client ID to edit: ");

        Client existing = service.findById(id);

        if (existing == null) {
            System.out.println("No client with such ID.");
            return;
        }

        String existingContact = existing.getContactDetails();

        System.out.printf("Current data: %s (%s), company: %s%n",
                existing.getName(),
                existingContact == null ? "" : existingContact,
                existing.getCompany().getName()
        );

        String newName = consoleHelper.readLine("New name (leave blank for no change): ");

        if (newName.isBlank()) {
            newName = existing.getName();
        }

        String newContact = consoleHelper.readLine("New contact details (leave blank for no change): ");

        if (newContact.isBlank()) {
            newContact = existing.getContactDetails();
        }

        ClientUpdateRequest request = new ClientUpdateRequest();
        request.setId(existing.getId());
        request.setName(newName);
        request.setContactDetails(newContact);

        Client updated = service.updateClient(request);

        String updatedContact = updated.getContactDetails();

        System.out.printf("Client updated: [%d] %s (%s), company: %s%n",
                updated.getId(),
                updated.getName(),
                updatedContact == null ? "" : updatedContact,
                updated.getCompany().getName()
        );
    }

    public static void handleDeleteClient(ClientService service) {
        long id = consoleHelper.readLong("Client ID to delete: ");

        service.deleteClient(id);

        System.out.println("If the client existed, it has been deleted.");
    }
}
