package persistence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import persistence.entity.Department;
import persistence.repository.DepartmentRepository;

import javax.validation.Valid;
import java.util.Optional;

@Service
public class DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentRepository departmentRepository;

    @Lazy
    private final EmployeeService employeeService;

    private final CompanyService companyService;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeService employeeService, CompanyService companyService) {
        this.departmentRepository = departmentRepository;
        this.employeeService = employeeService;
        this.companyService = companyService;
    }

    public Department create(@Valid Department department) {
        Assert.isNull(department.getId(), "employee id must be null");
        Assert.notNull(department.getCompany().getId(), "provided company id can't be null");

        companyService.findMust(department.getCompany().getId());
        if (department.getManager() != null) {
            Assert.notNull(department.getManager().getId(), "provided manager id can't be null");
            employeeService.findMust(department.getManager().getId());
        }

        department = departmentRepository.save(department);
        logger.debug("new department created: {}", department);
        return department;
    }

    public Department update(Department department) {
        logger.debug("request to update department : {}", department);
        if (department.getId() == null) {
            throw new IllegalArgumentException("invalid department parameter");
        }
        return departmentRepository.save(department);
    }

    Department findMust(Long id) {
        return departmentRepository.findMust(id);
    }

    public Optional<Department> findByCode(String code){
        return departmentRepository.findByCode(code);
    }

}
