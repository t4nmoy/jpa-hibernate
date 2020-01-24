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

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        testData.apply();
    }

    @Test
    @Transactional
    public void testCurdCompanies() {
        Company demo1 = companyService.create("DEMO1", "demo company 1", CompanyType.MARKETING);
        Company demo2 = companyService.create("DEMO2", "demo company 2", CompanyType.ENGINEERING);
        Company demo3 = companyService.create("DEMO3", "demo company 3", CompanyType.MARKETING);
        companyService.create("DEMO4", "demo company 4", CompanyType.ENGINEERING);
        companyService.create("DEMO5", "demo company 5", CompanyType.ENGINEERING);

        testData.getAlice().setCompany(demo1);
        testData.getBob().setCompany(demo1);
        demo1.addEmployee(testData.getAlice());
        demo1.addEmployee(testData.getBob());
        companyService.update(demo1);
        entityManager.flush();

        testData.getMark().setCompany(demo2);
        demo2.addEmployee(testData.getMark());
        companyService.update(demo2);
        entityManager.flush();

        testData.getTom().setCompany(demo3);
        demo3.addEmployee(testData.getTom());
        companyService.update(demo3);
        entityManager.flush();

        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.ASC, "code");
        List<Company> companies = companyService.findAll(pageable).toList();

        assertEquals(5, companies.size());
    }

}
