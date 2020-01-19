package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.Company;
import persistence.service.CompanyService;

import java.util.List;

@SpringBootTest
public class CompanyServiceTests {

    private static final Logger logger = LoggerFactory.getLogger(CompanyServiceTests.class);

    @Autowired
    private CompanyService companyService;

    @Autowired
    private AppTestData appTestData;

    @BeforeEach
    public void setup() {
        appTestData.createCompanies();
    }

    @Test
    @Transactional
    public void test1() {
        Pageable pageable = PageRequest.of(0, 100, Sort.Direction.DESC, "id");
        List<Company> companies = companyService.findAll(pageable).toList();

        companies.forEach(company -> logger.info("company : {}", company));
    }

    @Test
    @Transactional
    public void test2() {
        Pageable pageable = PageRequest.of(0, 100, Sort.Direction.DESC, "id");
        List<Company> companies = companyService.findAll(pageable).toList();

        companies.forEach(company -> logger.info("company : {}", company));
    }

}
