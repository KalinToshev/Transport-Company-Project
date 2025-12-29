package org.informatics.transportcompany.service;

import lombok.RequiredArgsConstructor;
import org.informatics.transportcompany.exceptions.company.NoCompanyWithProvidedIdException;
import org.informatics.transportcompany.exceptions.employee.NoEmployeeWithProvidedIdException;
import org.informatics.transportcompany.model.dto.employee.EmployeeCreateRequest;
import org.informatics.transportcompany.model.dto.employee.EmployeeUpdateRequest;
import org.informatics.transportcompany.model.entity.Employee;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.model.enums.EmployeeQualification;
import org.informatics.transportcompany.repository.employee.EmployeeRepository;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;

import java.util.List;

@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TransportCompanyRepository transportCompanyRepository;

    public Employee createEmployee(EmployeeCreateRequest request) {
        TransportCompany company = transportCompanyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new NoCompanyWithProvidedIdException("No company with id = " + request.getCompanyId()));

        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setQualification(request.getQualification());
        employee.setSalary(request.getSalary());
        employee.setCompany(company);

        return employeeRepository.create(employee);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public List<Employee> findAllOrderByQualificationThenSalary() {
        return employeeRepository.findAllOrderByQualificationThenSalary();
    }

    public List<Employee> findAllOrderBySalaryDesc() {
        return employeeRepository.findAllOrderBySalaryDesc();
    }

    public List<Employee> findByQualificationOrderBySalaryDesc(EmployeeQualification qualification) {
        return employeeRepository.findByQualificationOrderBySalaryDesc(qualification);
    }

    public Employee updateEmployee(EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(request.getId())
                .orElseThrow(() -> new NoEmployeeWithProvidedIdException("No employee with id = " + request.getId()));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setQualification(request.getQualification());
        employee.setSalary(request.getSalary());

        return employeeRepository.update(employee);
    }

    public void deleteEmployee(long id) {
        employeeRepository.deleteById(id);
    }
}