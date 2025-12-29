package org.informatics.transportcompany.handlers;

import org.informatics.transportcompany.ConsoleHelper;
import org.informatics.transportcompany.model.dto.employee.EmployeeCreateRequest;
import org.informatics.transportcompany.model.dto.employee.EmployeeUpdateRequest;
import org.informatics.transportcompany.model.entity.Employee;
import org.informatics.transportcompany.model.enums.EmployeeQualification;
import org.informatics.transportcompany.service.EmployeeService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class EmployeesHandler {

    private static final ConsoleHelper consoleHelper = new ConsoleHelper();

    public static void handleCreateEmployee(EmployeeService service) {
        long companyId = consoleHelper.readLong("Company ID: ");

        String firstName = consoleHelper.readLine("Employee first name: ");

        String lastName = consoleHelper.readLine("Employee last name: ");

        System.out.println("Qualification (choose one of): " +
                Arrays.toString(EmployeeQualification.values()));

        String qualStr = consoleHelper.readLine("Qualification: ").trim().toUpperCase();

        EmployeeQualification qualification;

        try {
            qualification = EmployeeQualification.valueOf(qualStr);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid qualification. Allowed: " +
                    Arrays.toString(EmployeeQualification.values()));
        }

        BigDecimal salary = consoleHelper.readBigDecimal("Salary (e.g. 2500.00): ");

        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setCompanyId(companyId);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setQualification(qualification);
        request.setSalary(salary);

        Employee e = service.createEmployee(request);

        System.out.println("Created employee with id = " + e.getId());
    }

    public static void handleListEmployees(EmployeeService service) {
        List<Employee> employees = service.findAll();

        if (employees.isEmpty()) {
            System.out.println("No registered employees.");
            return;
        }

        for (Employee e : employees) {
            System.out.printf("[%d] %s %s, qualification: %s, salary: %s, company: %s%n",
                    e.getId(),
                    e.getFirstName(),
                    e.getLastName(),
                    e.getQualification(),
                    e.getSalary(),
                    e.getCompany().getName()
            );
        }
    }

    public static void handleEditEmployee(EmployeeService service) {
        long id = consoleHelper.readLong("Employee ID to edit: ");

        Employee existing = service.findById(id);

        if (existing == null) {
            System.out.println("No employee with such ID.");
            return;
        }

        System.out.printf("Current data: %s %s, qualification: %s, salary: %s, company: %s%n",
                existing.getFirstName(),
                existing.getLastName(),
                existing.getQualification(),
                existing.getSalary(),
                existing.getCompany().getName()
        );

        String newFirstName = consoleHelper.readLine("New first name (leave blank for no change): ");

        if (newFirstName.isBlank()) {
            newFirstName = existing.getFirstName();
        }

        String newLastName = consoleHelper.readLine("New last name (leave blank for no change): ");

        if (newLastName.isBlank()) {
            newLastName = existing.getLastName();
        }

        System.out.println("New qualification (choose one of, leave blank for no change): "
                + Arrays.toString(EmployeeQualification.values()));

        String qualStr = consoleHelper.readLine("Qualification: ").trim().toUpperCase();

        EmployeeQualification newQualification;
        if (qualStr.isBlank()) {
            newQualification = existing.getQualification();
        } else {
            try {
                newQualification = EmployeeQualification.valueOf(qualStr);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid qualification. Allowed: " +
                        Arrays.toString(EmployeeQualification.values()));
            }
        }

        String salaryStr = consoleHelper.readLine("New salary (leave blank for no change): ");

        BigDecimal newSalary;

        if (salaryStr.isBlank()) {
            newSalary = existing.getSalary();
        } else {
            newSalary = new BigDecimal(salaryStr);
        }

        EmployeeUpdateRequest request = new EmployeeUpdateRequest();
        request.setId(id);
        request.setFirstName(newFirstName);
        request.setLastName(newLastName);
        request.setQualification(newQualification);
        request.setSalary(newSalary);

        Employee updated = service.updateEmployee(request);

        System.out.printf("Employee updated: [%d] %s %s, qualification: %s, salary: %s, company: %s%n",
                updated.getId(),
                updated.getFirstName(),
                updated.getLastName(),
                updated.getQualification(),
                updated.getSalary(),
                updated.getCompany().getName()
        );
    }

    public static void handleDeleteEmployee(EmployeeService service) {
        long id = consoleHelper.readLong("Employee ID to delete: ");

        service.deleteEmployee(id);

        System.out.println("If the employee existed, it has been deleted.");
    }

    public static void handleListEmployeesByQualification(EmployeeService service) {
        List<Employee> employees = service.findAllOrderByQualificationThenSalary();

        if (employees.isEmpty()) {
            System.out.println("No registered employees.");
            return;
        }

        for (Employee e : employees) {
            System.out.printf("[%d] %s %s, qualification: %s, salary: %s, company: %s%n",
                    e.getId(),
                    e.getFirstName(),
                    e.getLastName(),
                    e.getQualification(),
                    e.getSalary(),
                    e.getCompany().getName()
            );
        }
    }

    public static void handleListEmployeesBySalary(EmployeeService service) {
        List<Employee> employees = service.findAllOrderBySalaryDesc();

        if (employees.isEmpty()) {
            System.out.println("No registered employees.");
            return;
        }

        for (Employee e : employees) {
            System.out.printf("[%d] %s %s, qualification: %s, salary: %s, company: %s%n",
                    e.getId(),
                    e.getFirstName(),
                    e.getLastName(),
                    e.getQualification(),
                    e.getSalary(),
                    e.getCompany().getName()
            );
        }
    }

    public static void handleListEmployeesByQualificationFilter(EmployeeService service) {
        System.out.println("Qualification (choose one of): " +
                Arrays.toString(EmployeeQualification.values()));

        String qualStr = consoleHelper.readLine("Qualification: ").trim().toUpperCase();

        EmployeeQualification qualification;

        try {
            qualification = EmployeeQualification.valueOf(qualStr);
        } catch (IllegalArgumentException ex) {
            System.out.println("Invalid qualification. Allowed: " +
                    Arrays.toString(EmployeeQualification.values()));
            return;
        }

        List<Employee> employees = service.findByQualificationOrderBySalaryDesc(qualification);

        if (employees.isEmpty()) {
            System.out.println("No employees with this qualification.");
            return;
        }

        for (Employee e : employees) {
            System.out.printf("[%d] %s %s, qualification: %s, salary: %s, company: %s%n",
                    e.getId(),
                    e.getFirstName(),
                    e.getLastName(),
                    e.getQualification(),
                    e.getSalary(),
                    e.getCompany().getName()
            );
        }
    }
}
