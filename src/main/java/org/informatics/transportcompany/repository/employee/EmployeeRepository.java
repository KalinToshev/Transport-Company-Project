package org.informatics.transportcompany.repository.employee;

import org.informatics.transportcompany.model.entity.Employee;
import org.informatics.transportcompany.model.enums.EmployeeQualification;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {

    Employee create(Employee employee);

    Employee update(Employee employee);

    Optional<Employee> findById(long id);

    List<Employee> findAll();

    List<Employee> findAllOrderByQualificationThenSalary();

    List<Employee> findAllOrderBySalaryDesc();

    List<Employee> findByQualificationOrderBySalaryDesc(EmployeeQualification qualification);

    void deleteById(long id);
}
