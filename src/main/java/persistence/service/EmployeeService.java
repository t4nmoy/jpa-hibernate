package persistence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.Designation;
import persistence.entity.Employee;
import persistence.entity.EmployeeType;
import persistence.repository.EmployeeRepository;

import java.util.Optional;

@Service
@Transactional
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee save(String email, String name, Designation designation, EmployeeType employeeType) {
        Employee employee = Employee.of(email, name, designation, employeeType);
        employee =  employeeRepository.save(employee);
        logger.debug("new employee saved : {}", employee);
        return employee;
    }

    public Employee update(Employee employee) {
        logger.debug("request to update employee : {}", employee);
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public Page<Employee> findAll(Pageable pageable) {
        logger.debug("request to all employees");
        return employeeRepository.findAll(pageable);
    }


    @Transactional(readOnly = true)
    public Optional<Employee> findOne(Long id) {
        logger.info("request to get employee with id : {}", id);
        return employeeRepository.findById(id);
    }

    public void delete(Long id) {
        logger.debug("request to delete employee with id : {}", id);
        employeeRepository.deleteById(id);
    }

}
