package persistence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import persistence.entity.Company;
import persistence.entity.Department;
import persistence.entity.Employee;
import persistence.exception.NoSuchEntityException;
import persistence.repository.DepartmentRepository;

@Service
public class DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    private DepartmentRepository departmentRepository;

    private EmployeeService employeeService;

    private CompanyService companyService;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeService employeeService, CompanyService companyService) {
        this.departmentRepository = departmentRepository;
        this.employeeService = employeeService;
        this.companyService = companyService;
    }

    public Department create(String code, String title, Employee manager, Company company) {

        Assert.notNull(manager.getId(), String.format("invalid manager id : %s", manager.getId()));
        Assert.notNull(company.getId(), String.format("invalid company id : %s", company.getId()));

        if (!employeeService.findOne(manager.getId()).isPresent()) {
            throw new NoSuchEntityException(String.format("manager with id %s not found", manager.getId()));
        }

        if (!companyService.findOne(company.getId()).isPresent()) {
            throw new NoSuchEntityException(String.format("company with id %s not found", company.getId()));
        }

        Department department = Department.of(code, title, manager, company);
        department = departmentRepository.save(department);
        logger.debug("new department saved: {}", department);
        return department;
    }

    public Department update(Department department) {
        logger.debug("request to update department : {}", department);
        if (department.getId() == null) {
            throw new IllegalArgumentException("invalid department parameter");
        }
        return departmentRepository.save(department);
    }

}
