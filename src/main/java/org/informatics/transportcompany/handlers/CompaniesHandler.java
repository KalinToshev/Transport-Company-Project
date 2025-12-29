package org.informatics.transportcompany.handlers;

import org.informatics.transportcompany.ConsoleHelper;
import org.informatics.transportcompany.model.dto.transportCompany.TransportCompanyCreateRequest;
import org.informatics.transportcompany.model.dto.transportCompany.TransportCompanyUpdateRequest;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.service.TransportCompanyService;

import java.math.BigDecimal;
import java.util.List;

public class CompaniesHandler {

    private static final ConsoleHelper consoleHelper = new ConsoleHelper();

    public static void handleCreateCompany(TransportCompanyService service) {
        String name = consoleHelper.readLine("Company name: ");

        String address = consoleHelper.readLine("Address: ");

        TransportCompanyCreateRequest request = new TransportCompanyCreateRequest();
        request.setName(name);
        request.setAddress(address);

        TransportCompany c = service.createCompany(request);

        System.out.println("Created company with id = " + c.getId());
    }

    public static void handleListCompanies(TransportCompanyService service) {
        List<TransportCompany> companies = service.findAll();

        if (companies.isEmpty()) {
            System.out.println("No companies created.");
            return;
        }

        for (TransportCompany c : companies) {
            System.out.printf("[%d] %s (%s)%n", c.getId(), c.getName(),
                    c.getAddress() == null ? "" : c.getAddress());
        }
    }

    public static void handleListCompaniesByName(TransportCompanyService service) {
        List<TransportCompany> companies = service.findAllOrderByName();

        if (companies.isEmpty()) {
            System.out.println("No companies created.");
            return;
        }

        for (TransportCompany c : companies) {
            System.out.printf("[%d] %s (%s)%n",
                    c.getId(),
                    c.getName(),
                    c.getAddress() == null ? "" : c.getAddress());
        }
    }

    public static void handleListCompaniesByRevenue(TransportCompanyService service) {
        List<Object[]> rows = service.findAllWithRevenueOrderByRevenueDesc();

        if (rows.isEmpty()) {
            System.out.println("No companies created.");
            return;
        }

        for (Object[] row : rows) {
            TransportCompany company = (TransportCompany) row[0];
            BigDecimal revenue = (BigDecimal) row[1];

            System.out.printf("[%d] %s (%s) - total revenue: %s%n",
                    company.getId(),
                    company.getName(),
                    company.getAddress() == null ? "" : company.getAddress(),
                    revenue
            );
        }
    }

    public static void handleEditCompany(TransportCompanyService service) {
        long id = consoleHelper.readLong("Company ID to edit: ");

        TransportCompany existing = service.findById(id);

        if (existing == null) {
            System.out.println("No company with such ID.");
            return;
        }

        System.out.printf("Current data: %s (%s)%n",
                existing.getName(),
                existing.getAddress() == null ? "" : existing.getAddress());

        String newName = consoleHelper.readLine("New name (leave blank for no change): ");

        if (newName.isBlank()) {
            newName = existing.getName();
        }

        String newAddress = consoleHelper.readLine("New address (leave blank for no change): ");

        if (newAddress.isBlank()) {
            newAddress = existing.getAddress();
        }

        TransportCompanyUpdateRequest updateRequest = new TransportCompanyUpdateRequest();
        updateRequest.setId(id);
        updateRequest.setName(newName);
        updateRequest.setAddress(newAddress);

        TransportCompany updated = service.updateCompany(updateRequest);

        System.out.printf("Company updated: %s (%s)%n",
                updated.getName(),
                updated.getAddress() == null ? "" : updated.getAddress());
    }

    public static void handleDeleteCompany(TransportCompanyService service) {
        long id = consoleHelper.readLong("Company ID to delete: ");

        service.deleteCompany(id);

        System.out.println("If the company existed, it has been deleted.");
    }
}
