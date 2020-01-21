package persistence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import persistence.entity.Company;
import persistence.entity.Department;
import persistence.entity.Employee;
import persistence.repository.DepartmentRepository;

@Service
public class DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    private DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department save(String title, Employee manager, Company company) {
        Department department = Department.of(title, manager, company);
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
