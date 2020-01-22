package persistence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import persistence.entity.Company;
import persistence.entity.Designation;
import persistence.entity.Employee;
import persistence.entity.EmployeeType;
import persistence.exception.NoSuchEntityException;
import persistence.repository.EmployeeRepository;

import java.util.Optional;

@Service
@Transactional
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private EmployeeRepository employeeRepository;

    private CompanyService companyService;

    public EmployeeService(EmployeeRepository employeeRepository, CompanyService companyService) {
        this.employeeRepository = employeeRepository;
        this.companyService = companyService;
    }

    public Employee create(Employee employee) {

        Assert.isNull(employee.getId(), "");

        Assert.notNull(company.getId(), String.format("invalid company id : %s", company.getId()));

        if (!companyService.findOne(company.getId()).isPresent()) {
            throw new NoSuchEntityException(String.format("company with id %s not found", company.getId()));
        }

        Employee employee = Employee.of(company, email, name, designation, employeeType);
        employee =  employeeRepository.save(employee);
        logger.debug("new employee saved : {}", employee);
        return employee;
    }

    public Employee update(Employee employee) {
        logger.debug("request to update employee : {}", employee);
        if (employee.getId() == null) {
            throw new IllegalArgumentException("invalid employee parameter");
        }
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public Page<Employee> findAll(Pageable pageable) {
        logger.debug("request to get all employees with paging : {}", pageable);
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

    @Transactional(readOnly = true)
    public Employee findMust(Long id) {
        logger.info("request to must get employee with id : {}", id);
        return employeeRepository.findMust(id);
    }

}
