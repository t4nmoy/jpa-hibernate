package persistence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import persistence.entity.Employee;
import persistence.repository.EmployeeRepository;

import javax.validation.Valid;
import java.util.Optional;

@Service
@Validated
@Transactional
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private EmployeeRepository employeeRepository;

    private CompanyService companyService;

    private DepartmentService departmentService;

    public EmployeeService(EmployeeRepository employeeRepository, CompanyService companyService, @Lazy DepartmentService departmentService) {
        this.employeeRepository = employeeRepository;
        this.companyService = companyService;
        this.departmentService = departmentService;
    }

    public Employee create(@Valid Employee employee) {
        Assert.isNull(employee.getId(), "employee id must be null");
        validateEmployee(employee);

        employee = employeeRepository.save(employee);
        logger.debug("new employee created : {}", employee);
        return employee;
    }

    public Employee update(Employee employee) {
        logger.debug("request to update employee : {}", employee);

        Assert.isTrue(employee.getId() != null, "employee id must not be null");
        validateEmployee(employee);

        if (employee.getId() == null) {
            throw new IllegalArgumentException("invalid employee parameter");
        }
        return employeeRepository.save(employee);
    }

    private void validateEmployee(Employee employee) {
        Assert.notNull(employee.getCompany().getId(), "company id is required");
        Assert.notNull(employee.getDepartment().getId(), "department id is required");

        companyService.findMust(employee.getCompany().getId());
        departmentService.findMust(employee.getDepartment().getId());

        Assert.isTrue(employee.getCompany().equals(employee.getDepartment().getCompany()),
                "department's company is different than employee's company");
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
