package persistence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import persistence.entity.Designation;
import persistence.entity.Employee;
import persistence.entity.EmployeeType;
import persistence.repository.EmployeeRepository;

import javax.transaction.Transactional;

@Service
@Transactional
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee save(String email, String name, Designation designation, EmployeeType employeeType) {
        Employee emp = Employee.of(email, name, designation, employeeType);
        return employeeRepository.save(emp);
    }


}
