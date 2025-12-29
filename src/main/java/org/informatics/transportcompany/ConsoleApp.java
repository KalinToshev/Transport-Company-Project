package org.informatics.transportcompany;

import org.informatics.transportcompany.config.HibernateUtil;
import org.informatics.transportcompany.repository.client.ClientRepository;
import org.informatics.transportcompany.repository.client.ClientRepositoryImpl;
import org.informatics.transportcompany.repository.employee.EmployeeRepository;
import org.informatics.transportcompany.repository.employee.EmployeeRepositoryImpl;
import org.informatics.transportcompany.repository.transport.TransportRepository;
import org.informatics.transportcompany.repository.transport.TransportRepositoryImpl;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepositoryImpl;
import org.informatics.transportcompany.repository.vehicle.VehicleRepository;
import org.informatics.transportcompany.repository.vehicle.VehicleRepositoryImpl;
import org.informatics.transportcompany.service.ClientService;
import org.informatics.transportcompany.service.EmployeeService;
import org.informatics.transportcompany.service.TransportCompanyService;
import org.informatics.transportcompany.service.TransportService;
import org.informatics.transportcompany.service.VehicleService;

import static org.informatics.transportcompany.handlers.ClientsHandler.handleCreateClient;
import static org.informatics.transportcompany.handlers.ClientsHandler.handleDeleteClient;
import static org.informatics.transportcompany.handlers.ClientsHandler.handleEditClient;
import static org.informatics.transportcompany.handlers.ClientsHandler.handleListClients;
import static org.informatics.transportcompany.handlers.CompaniesHandler.handleCreateCompany;
import static org.informatics.transportcompany.handlers.CompaniesHandler.handleDeleteCompany;
import static org.informatics.transportcompany.handlers.CompaniesHandler.handleEditCompany;
import static org.informatics.transportcompany.handlers.CompaniesHandler.handleListCompanies;
import static org.informatics.transportcompany.handlers.CompaniesHandler.handleListCompaniesByName;
import static org.informatics.transportcompany.handlers.CompaniesHandler.handleListCompaniesByRevenue;
import static org.informatics.transportcompany.handlers.EmployeesHandler.handleCreateEmployee;
import static org.informatics.transportcompany.handlers.EmployeesHandler.handleDeleteEmployee;
import static org.informatics.transportcompany.handlers.EmployeesHandler.handleEditEmployee;
import static org.informatics.transportcompany.handlers.EmployeesHandler.handleListEmployees;
import static org.informatics.transportcompany.handlers.EmployeesHandler.handleListEmployeesByQualification;
import static org.informatics.transportcompany.handlers.EmployeesHandler.handleListEmployeesByQualificationFilter;
import static org.informatics.transportcompany.handlers.EmployeesHandler.handleListEmployeesBySalary;
import static org.informatics.transportcompany.handlers.TransportsHandler.handleCreateTransport;
import static org.informatics.transportcompany.handlers.TransportsHandler.handleExportTransportsToFile;
import static org.informatics.transportcompany.handlers.TransportsHandler.handleFilterTransportsByDestination;
import static org.informatics.transportcompany.handlers.TransportsHandler.handleListTransports;
import static org.informatics.transportcompany.handlers.TransportsHandler.handleListTransportsByDestination;
import static org.informatics.transportcompany.handlers.TransportsHandler.handleMarkTransportPaid;
import static org.informatics.transportcompany.handlers.TransportsHandler.handleShowCompanyRevenueForPeriod;
import static org.informatics.transportcompany.handlers.TransportsHandler.handleShowDriverRevenue;
import static org.informatics.transportcompany.handlers.TransportsHandler.handleShowDriverTransportStats;
import static org.informatics.transportcompany.handlers.TransportsHandler.handleShowTransportsFromFile;
import static org.informatics.transportcompany.handlers.TransportsHandler.handleShowTransportsSummary;
import static org.informatics.transportcompany.handlers.VehiclesHandler.handleCreateVehicle;
import static org.informatics.transportcompany.handlers.VehiclesHandler.handleDeleteVehicle;
import static org.informatics.transportcompany.handlers.VehiclesHandler.handleEditVehicle;
import static org.informatics.transportcompany.handlers.VehiclesHandler.handleListVehicles;

public class ConsoleApp {

    private static final ConsoleHelper consoleHelper = new ConsoleHelper();

    public static void main(String[] args) {
        TransportCompanyRepository transportCompanyRepository = new TransportCompanyRepositoryImpl();
        ClientRepository clientRepository = new ClientRepositoryImpl();
        VehicleRepository vehicleRepository = new VehicleRepositoryImpl();
        EmployeeRepository employeeRepository = new EmployeeRepositoryImpl();
        TransportRepository transportRepository = new TransportRepositoryImpl();

        TransportCompanyService companyService = new TransportCompanyService(transportCompanyRepository);
        ClientService clientService = new ClientService(clientRepository, transportCompanyRepository);
        TransportService transportService = new TransportService(
                transportRepository,
                transportCompanyRepository,
                clientRepository,
                vehicleRepository,
                employeeRepository
        );
        VehicleService vehicleService = new VehicleService(vehicleRepository, transportCompanyRepository);
        EmployeeService employeeService = new EmployeeService(employeeRepository, transportCompanyRepository);

        System.out.println("=== Application for a transport company ===");
        printHelp();

        while (true) {
            String line = consoleHelper.readLine("> ").trim();
            if (line.isEmpty()) continue;

            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                switch (line) {
                    case "help" -> printHelp();

                    /* Company commands */
                    case "create-company" -> handleCreateCompany(companyService);
                    case "list-companies" -> handleListCompanies(companyService);
                    case "list-companies-by-name" -> handleListCompaniesByName(companyService);
                    case "list-companies-by-revenue" -> handleListCompaniesByRevenue(companyService);
                    case "edit-company" -> handleEditCompany(companyService);
                    case "delete-company" -> handleDeleteCompany(companyService);

                    /* Client commands */
                    case "create-client" -> handleCreateClient(clientService);
                    case "list-clients" -> handleListClients(clientService);
                    case "edit-client" -> handleEditClient(clientService);
                    case "delete-client" -> handleDeleteClient(clientService);

                    /* Vehicle commands */
                    case "create-vehicle" -> handleCreateVehicle(vehicleService);
                    case "list-vehicles" -> handleListVehicles(vehicleService);
                    case "edit-vehicle" -> handleEditVehicle(vehicleService);
                    case "delete-vehicle" -> handleDeleteVehicle(vehicleService);

                    /* Employee commands */
                    case "create-employee" -> handleCreateEmployee(employeeService);
                    case "list-employees" -> handleListEmployees(employeeService);
                    case "list-employees-by-qualification" -> handleListEmployeesByQualification(employeeService);
                    case "list-employees-by-salary" -> handleListEmployeesBySalary(employeeService);
                    case "list-employees-by-qualification-filter" ->
                            handleListEmployeesByQualificationFilter(employeeService);
                    case "edit-employee" -> handleEditEmployee(employeeService);
                    case "delete-employee" -> handleDeleteEmployee(employeeService);

                    /* Transport commands */
                    case "create-transport" -> handleCreateTransport(transportService);
                    case "list-transports" -> handleListTransports(transportService);
                    case "list-transports-by-destination" -> handleListTransportsByDestination(transportService);
                    case "filter-transports-by-destination" -> handleFilterTransportsByDestination(transportService);
                    case "mark-transport-paid" -> handleMarkTransportPaid(transportService);
                    case "report-transports-summary" -> handleShowTransportsSummary(transportService);
                    case "report-driver-transports" -> handleShowDriverTransportStats(transportService);
                    case "report-company-revenue-period" -> handleShowCompanyRevenueForPeriod(transportService);
                    case "report-driver-revenue" -> handleShowDriverRevenue(transportService);
                    case "export-transports-to-file" -> handleExportTransportsToFile(transportService);
                    case "show-transports-from-file" -> handleShowTransportsFromFile();

                    default -> System.out.println("Unknown command. Type 'help' for a list.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }

        HibernateUtil.shutdown();
        System.out.println("Goodbye!");
    }

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  help              - shows this menu");
        System.out.println("  create-company    - create a transport company");
        System.out.println("  list-companies    - list all companies");
        System.out.println("  list-companies-by-name     - companies sorted by name");
        System.out.println("  list-companies-by-revenue  - companies sorted by total revenue");
        System.out.println("  edit-company      - edit a transport company by id");
        System.out.println("  delete-company    - delete a company by id");
        System.out.println("  create-client     - create a client for a company");
        System.out.println("  list-clients      - list all clients");
        System.out.println("  edit-client       - edit a client by id");
        System.out.println("  delete-client     - delete a client by id");
        System.out.println("  create-vehicle    - create a vehicle for a company");
        System.out.println("  list-vehicles     - list all vehicles");
        System.out.println("  edit-vehicle      - edit a vehicle by id");
        System.out.println("  delete-vehicle    - delete a vehicle by id");
        System.out.println("  create-employee   - create an employee for a company");
        System.out.println("  list-employees    - list all employees");
        System.out.println("  list-employees-by-qualification        - employees sorted by qualification and salary");
        System.out.println("  list-employees-by-salary               - employees sorted by salary (descending)");
        System.out.println("  list-employees-by-qualification-filter - employees with selected qualification, sorted by salary");
        System.out.println("  edit-employee     - edit an employee by id");
        System.out.println("  delete-employee   - delete an employee by id");
        System.out.println("  create-transport  - create a transport");
        System.out.println("  list-transports   - list all transports");
        System.out.println("  list-transports-by-destination   - transports sorted by destination");
        System.out.println("  filter-transports-by-destination - transports filtered by selected destination");
        System.out.println("  mark-transport-paid - mark a transport as paid by id");
        System.out.println("  report-transports-summary          - total number of transports and total amount");
        System.out.println("  report-driver-transports           - drivers and count of completed transports");
        System.out.println("  report-company-revenue-period      - revenue of a selected company for a selected period");
        System.out.println("  report-driver-revenue              - total revenue (paid transports) by drivers");
        System.out.println("  export-transports-to-file          - save all transports to a file");
        System.out.println("  show-transports-from-file          - display transports from a file");
        System.out.println("  exit              - exit the program");
    }
}
