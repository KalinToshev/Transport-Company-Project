package org.informatics.transportcompany.handlers;

import org.informatics.transportcompany.ConsoleHelper;
import org.informatics.transportcompany.model.dto.transport.CalculateCompanyRevenueForPeriodRequest;
import org.informatics.transportcompany.model.dto.transport.TransportCreateRequest;
import org.informatics.transportcompany.model.entity.Employee;
import org.informatics.transportcompany.model.entity.Transport;
import org.informatics.transportcompany.service.TransportService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransportsHandler {

    private static final ConsoleHelper consoleHelper = new ConsoleHelper();

    public static void handleCreateTransport(TransportService service) {
        long companyId = consoleHelper.readLong("Company ID: ");

        long clientId = consoleHelper.readLong("Client ID: ");

        long vehicleId = consoleHelper.readLong("Vehicle ID: ");

        long driverId = consoleHelper.readLong("Driver ID (employee): ");

        String from = consoleHelper.readLine("From location: ");

        String to = consoleHelper.readLine("To location: ");

        LocalDateTime departure = consoleHelper.readDateTime("Departure date and time (format 2025-11-18T10:00): ");

        LocalDateTime arrival = consoleHelper.readDateTime("Arrival date and time (format 2025-11-18T15:30): ");

        String cargoDesc = consoleHelper.readLine("Cargo/passenger description: ");

        String weightStr = consoleHelper.readLine("Total cargo weight (kg, empty if not applicable): ");

        Double weight = weightStr.isBlank() ? null : Double.parseDouble(weightStr);

        BigDecimal price = consoleHelper.readBigDecimal("Price: ");

        boolean paid = consoleHelper.readLine("Is the transport paid? (yes/no): ").trim().equalsIgnoreCase("yes");

        TransportCreateRequest request = new TransportCreateRequest();
        request.setCompanyId(companyId);
        request.setClientId(clientId);
        request.setVehicleId(vehicleId);
        request.setDriverId(driverId);
        request.setFromLocation(from);
        request.setToLocation(to);
        request.setDeparture(departure);
        request.setArrival(arrival);
        request.setCargoDescription(cargoDesc);
        request.setCargoWeight(weight);
        request.setPrice(price);
        request.setPaid(paid);

        Transport t = service.createTransport(request);

        System.out.println("Created transport with id = " + t.getId());
    }

    public static void handleListTransports(TransportService service) {
        List<Transport> transports = service.findAll();

        if (checkForRegisteredTransports(transports)) return;

        for (Transport t : transports) {
            String paidLabel = t.isPaid() ? "PAID" : "UNPAID";
            System.out.printf("[%d] %s -> %s, client: %s, price: %s, status: %s%n",
                    t.getId(),
                    t.getFromLocation(),
                    t.getToLocation(),
                    t.getClient().getName(),
                    t.getPrice(),
                    paidLabel
            );
        }
    }

    public static void handleMarkTransportPaid(TransportService service) {
        long id = consoleHelper.readLong("Transport ID to mark as paid: ");

        Transport updated = service.markPaid(id);

        System.out.printf(
                "Transport [%d] from %s to %s for client %s has been marked as PAID. Amount: %s%n",
                updated.getId(),
                updated.getFromLocation(),
                updated.getToLocation(),
                updated.getClient().getName(),
                updated.getPrice()
        );
    }

    public static void handleListTransportsByDestination(TransportService service) {
        List<Transport> transports = service.findAllOrderByToLocation();

        if (checkForRegisteredTransports(transports)) return;

        for (Transport t : transports) {
            String paidLabel = t.isPaid() ? "PAID" : "UNPAID";
            System.out.printf("[%d] %s -> %s, client: %s, price: %s, status: %s%n",
                    t.getId(),
                    t.getFromLocation(),
                    t.getToLocation(),
                    t.getClient().getName(),
                    t.getPrice(),
                    paidLabel
            );
        }
    }

    public static void handleFilterTransportsByDestination(TransportService service) {
        String destination = consoleHelper.readLine("Destination (to location): ");

        if (destination.isEmpty()) {
            System.out.println("Destination cannot be empty.");
            return;
        }

        List<Transport> transports = service.findByToLocation(destination);

        if (transports.isEmpty()) {
            System.out.println("No transports to this destination.");
            return;
        }

        for (Transport t : transports) {
            String paidLabel = t.isPaid() ? "PAID" : "UNPAID";
            System.out.printf("[%d] %s -> %s, client: %s, price: %s, status: %s%n",
                    t.getId(),
                    t.getFromLocation(),
                    t.getToLocation(),
                    t.getClient().getName(),
                    t.getPrice(),
                    paidLabel
            );
        }
    }

    public static void handleShowTransportsSummary(TransportService service) {
        long count = service.countAllTransports();

        if (count == 0) {
            System.out.println("No registered transports.");
            return;
        }

        BigDecimal total = service.calculateTotalRevenue();

        System.out.println("=== Transports summary ===");
        System.out.printf("Total number of transports: %d%n", count);
        System.out.printf("Total revenue from transports: %s%n", total);
    }

    public static void handleShowDriverTransportStats(TransportService service) {
        List<Object[]> rows = service.findDriverTransportStats();

        if (rows.isEmpty()) {
            System.out.println("No registered transports.");
            return;
        }

        System.out.println("=== Report: transports per driver ===");

        for (Object[] row : rows) {
            Employee driver = (Employee) row[0];
            Long count = (Long) row[1];

            System.out.printf(
                    "[%d] %s %s, qualification: %s, company: %s - number of transports: %d%n",
                    driver.getId(),
                    driver.getFirstName(),
                    driver.getLastName(),
                    driver.getQualification(),
                    driver.getCompany().getName(),
                    count
            );
        }
    }

    public static void handleShowCompanyRevenueForPeriod(TransportService service) {
        long companyId = consoleHelper.readLong("Company ID: ");

        LocalDateTime from = consoleHelper.readDateTime(
                "Start date and time (format 2025-01-01T00:00): "
        );

        LocalDateTime to = consoleHelper.readDateTime(
                "End date and time (format 2025-12-31T23:59): "
        );

        CalculateCompanyRevenueForPeriodRequest request = new CalculateCompanyRevenueForPeriodRequest();
        request.setCompanyId(companyId);
        request.setFrom(from);
        request.setTo(to);

        try {
            BigDecimal revenue = service.calculateCompanyRevenueForPeriod(request);

            System.out.println("=== Report: company revenue for period ===");
            System.out.printf("Company ID: %d%n", companyId);
            System.out.printf("Period: from %s to %s%n", from, to);
            System.out.printf("Total revenue (paid transports only): %s%n", revenue);
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public static void handleShowDriverRevenue(TransportService service) {
        List<Object[]> rows = service.findDriverRevenue();

        if (rows.isEmpty()) {
            System.out.println("No registered transports.");
            return;
        }

        System.out.println("=== Report: revenue per driver (paid transports only) ===");

        for (Object[] row : rows) {
            Employee driver = (Employee) row[0];
            BigDecimal revenue = (BigDecimal) row[1];

            System.out.printf(
                    "[%d] %s %s, qualification: %s, company: %s - total revenue: %s%n",
                    driver.getId(),
                    driver.getFirstName(),
                    driver.getLastName(),
                    driver.getQualification(),
                    driver.getCompany().getName(),
                    revenue
            );
        }
    }

    public static void handleExportTransportsToFile(TransportService service) {
        String filename = consoleHelper.readLine(
                "Filename to write (e.g. transports.txt): "
        ).trim();

        if (filename.isEmpty()) {
            System.out.println("Filename cannot be empty.");
            return;
        }

        List<Transport> transports = service.findAll();

        if (checkForRegisteredTransports(transports)) {
            System.out.println("No transports to write to file.");
            return;
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("id;company;client;vehicle;driver;from;to;departure;arrival;price;paid;cargoDescription;cargoWeight");

            for (Transport t : transports) {
                String driverName = t.getDriver().getFirstName() + " " + t.getDriver().getLastName();
                String departureStr = t.getDepartureDateTime() == null
                        ? ""
                        : t.getDepartureDateTime().toString();
                String arrivalStr = t.getArrivalDateTime() == null
                        ? ""
                        : t.getArrivalDateTime().toString();
                String cargoDesc = t.getCargoDescription() == null
                        ? ""
                        : t.getCargoDescription();
                String cargoWeightStr = t.getCargoWeight() == null
                        ? ""
                        : t.getCargoWeight().toString();

                out.printf("%d;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s%n",
                        t.getId(),
                        t.getCompany().getName(),
                        t.getClient().getName(),
                        t.getVehicle().getRegistrationNumber(),
                        driverName,
                        t.getFromLocation(),
                        t.getToLocation(),
                        departureStr,
                        arrivalStr,
                        t.getPrice().toPlainString(),
                        t.isPaid(),
                        cargoDesc,
                        cargoWeightStr
                );
            }

            System.out.printf("Successfully wrote %d transports to file '%s'.%n",
                    transports.size(), filename);

        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void handleShowTransportsFromFile() {
        String filename = consoleHelper.readLine(
                "Filename to read (e.g. transports.txt): "
        ).trim();

        if (filename.isEmpty()) {
            System.out.println("Filename cannot be empty.");
            return;
        }

        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File '" + filename + "' does not exist.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();

            if (header == null) {
                System.out.println("The file is empty.");
                return;
            }

            String line;
            int count = 0;

            System.out.println("Transports read from file:");
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(";", -1);
                if (parts.length < 13) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                String idStr = parts[0];
                String company = parts[1];
                String client = parts[2];
                String vehicle = parts[3];
                String driver = parts[4];
                String from = parts[5];
                String to = parts[6];
                String departure = parts[7];
                String arrival = parts[8];
                String priceStr = parts[9];
                String paidStr = parts[10];
                String cargoDescription = parts[11];
                String cargoWeight = parts[12];

                try {
                    long id = Long.parseLong(idStr);
                    BigDecimal price = new BigDecimal(priceStr);
                    boolean paid = Boolean.parseBoolean(paidStr);
                    String paidLabel = paid ? "PAID" : "UNPAID";

                    System.out.printf(
                            "[%d] %s -> %s, client: %s, price: %s, status: %s, company: %s, vehicle: %s, driver: %s, departure: %s, arrival: %s, cargo: %s, weight: %s%n",
                            id,
                            from,
                            to,
                            client,
                            price,
                            paidLabel,
                            company,
                            vehicle,
                            driver,
                            departure,
                            arrival,
                            cargoDescription.isEmpty() ? "N/A" : cargoDescription,
                            cargoWeight.isEmpty() ? "N/A" : cargoWeight
                    );
                    count++;
                } catch (NumberFormatException ex) {
                    System.out.println("Skipping line with invalid numbers: " + line);
                }
            }

            if (count == 0) {
                System.out.println("No valid transports found in the file.");
            }

        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
    }

    private static boolean checkForRegisteredTransports(List<Transport> transports) {
        if (transports.isEmpty()) {
            System.out.println("No registered transports.");
            return true;
        }

        return false;
    }
}
