package org.informatics.transportcompany.handlers;

import org.informatics.transportcompany.ConsoleHelper;
import org.informatics.transportcompany.model.dto.vehicle.VehicleCreateRequest;
import org.informatics.transportcompany.model.dto.vehicle.VehicleUpdateRequest;
import org.informatics.transportcompany.model.entity.Vehicle;
import org.informatics.transportcompany.model.enums.VehicleType;
import org.informatics.transportcompany.service.VehicleService;

import java.util.Arrays;
import java.util.List;

public class VehiclesHandler {

    private static final ConsoleHelper consoleHelper = new ConsoleHelper();

    public static void handleCreateVehicle(VehicleService service) {
        long companyId = consoleHelper.readLong("Company ID: ");

        String regNumber = consoleHelper.readLine("Registration number: ");

        System.out.println("Vehicle type (choose one of): " +
                Arrays.toString(VehicleType.values()));

        String typeStr = consoleHelper.readLine("Type: ").trim().toUpperCase();

        VehicleType type;
        try {
            type = VehicleType.valueOf(typeStr);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid vehicle type. Allowed: " +
                    Arrays.toString(VehicleType.values()));
        }

        int capacity = consoleHelper.readInt("Capacity (number of seats or kg): ");

        VehicleCreateRequest request = new VehicleCreateRequest();
        request.setCompanyId(companyId);
        request.setRegistrationNumber(regNumber);
        request.setType(type);
        request.setCapacity(capacity);

        Vehicle v = service.createVehicle(request);

        System.out.println("Created vehicle with id = " + v.getId());
    }

    public static void handleListVehicles(VehicleService service) {
        List<Vehicle> vehicles = service.findAll();

        if (vehicles.isEmpty()) {
            System.out.println("No registered vehicles.");
            return;
        }

        for (Vehicle v : vehicles) {
            System.out.printf("[%d] %s, type: %s, capacity: %d, company: %s%n",
                    v.getId(),
                    v.getRegistrationNumber(),
                    v.getType(),
                    v.getCapacity(),
                    v.getCompany().getName()
            );
        }
    }

    public static void handleEditVehicle(VehicleService service) {
        long id = consoleHelper.readLong("Vehicle ID to edit: ");

        Vehicle existing = service.findById(id);

        if (existing == null) {
            System.out.println("No vehicle with such ID.");
            return;
        }

        System.out.printf("Current data: %s, type: %s, capacity: %d, company: %s%n",
                existing.getRegistrationNumber(),
                existing.getType(),
                existing.getCapacity(),
                existing.getCompany().getName()
        );

        String newReg = consoleHelper.readLine("New registration number (leave blank for no change): ");

        if (newReg.isBlank()) {
            newReg = existing.getRegistrationNumber();
        }

        System.out.println("Vehicle type (choose one of): " +
                Arrays.toString(VehicleType.values()));

        String typeStr = consoleHelper.readLine("New type (leave blank for no change): ").trim();

        VehicleType newType;
        if (typeStr.isBlank()) {
            newType = existing.getType();
        } else {
            try {
                newType = VehicleType.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid vehicle type. Allowed: " +
                        Arrays.toString(VehicleType.values()));
            }
        }

        String capacityStr = consoleHelper.readLine("New capacity (number of seats or kg, leave blank for no change): ");

        int newCapacity;
        if (capacityStr.isBlank()) {
            newCapacity = existing.getCapacity();
        } else {
            newCapacity = Integer.parseInt(capacityStr);
        }

        VehicleUpdateRequest updateRequest = new VehicleUpdateRequest();
        updateRequest.setId(id);
        updateRequest.setRegistrationNumber(newReg);
        updateRequest.setType(newType);
        updateRequest.setCapacity(newCapacity);

        Vehicle updated = service.updateVehicle(updateRequest);

        System.out.printf("Vehicle updated: [%d] %s, type: %s, capacity: %d, company: %s%n",
                updated.getId(),
                updated.getRegistrationNumber(),
                updated.getType(),
                updated.getCapacity(),
                updated.getCompany().getName()
        );
    }

    public static void handleDeleteVehicle(VehicleService service) {
        long id = consoleHelper.readLong("Vehicle ID to delete: ");

        service.deleteVehicle(id);

        System.out.println("If the vehicle existed, it has been deleted.");
    }
}
