package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.*;
import persistence.service.CompanyService;
import persistence.service.DepartmentService;
import persistence.service.EmployeeService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CompanyServiceTests {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private TestData testData;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        testData.createEmployees();
    }

    @Test
    @Transactional
    public void testCurdCompanies() {
        Company demo1 = companyService.create("DEMO1", "demo company 1", CompanyType.PROVIDER);
        Company demo2 = companyService.create("DEMO2", "demo company 2", CompanyType.PROVIDER);
        Company demo3 = companyService.create("DEMO3", "demo company 3", CompanyType.PROVIDER);
        companyService.create("DEMO4", "demo company 4", CompanyType.PROVIDER);
        companyService.create("DEMO5", "demo company 5", CompanyType.PROVIDER);




        demo1.addEmployee(testData.getAlice());
        demo1.addEmployee(testData.getBob());
        companyService.update(demo1);
        entityManager.flush();

        demo2.addEmployee(testData.getMark());
        companyService.update(demo2);
        entityManager.flush();

        demo3.addEmployee(testData.getTom());
        companyService.update(demo3);
        entityManager.flush();

        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "code");
        List<Company> companies = companyService.findAll(pageable).toList();

        assertEquals(5, companies.size());
    }

}
