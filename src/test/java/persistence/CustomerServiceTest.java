package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.Company;
import persistence.entity.Customer;
import persistence.entity.PhoneNumber;
import persistence.service.CompanyService;
import persistence.service.CustomerService;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private TestData testData;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setup(){
        testData.apply();
    }

    @Test
    @Transactional
    void curdCustomerTest() {
        Optional<Company> rootCompany = companyService.findByCode(Company.ROOT_COMPANY);
        assertTrue(rootCompany.isPresent());

        Customer demo1 = new Customer("demo1", rootCompany.get());
        demo1 = customerService.create(demo1);
        assertNotNull(demo1.getId());

        Customer demo2 = new Customer("demo2", rootCompany.get());
        demo2 = customerService.create(demo2);
        assertNotNull(demo2.getId());

        PhoneNumber num1 = new PhoneNumber("0000001", PhoneNumber.Type.HOME);
        demo1.addPhone(num1);

        PhoneNumber num2 = new PhoneNumber("0000002", PhoneNumber.Type.OFFICE);
        demo1.addPhone(num2);

        customerService.update(demo1);


        entityManager.flush();
        entityManager.clear();

        demo1 = customerService.findMust(demo1.getId());
        assertNotNull(demo1);
        assertNotNull(demo1.getId());
        assertTrue(demo1.getPhones().size() == 2);

        PhoneNumber num3 = new PhoneNumber("0000003", PhoneNumber.Type.OFFICE);
        demo1.addPhone(num3);
        demo1 = customerService.findMust(demo1.getId());

        customerService.update(demo1);

        entityManager.flush();
        entityManager.clear();

        assertTrue(demo1.getPhones().size() == 3);


        demo2.addItemQuantity("item1", Integer.valueOf(11));
        demo2.addItemQuantity("item2", Integer.valueOf(12));
        customerService.update(demo2);

        entityManager.flush();
        entityManager.clear();

        demo2 = customerService.findMust(demo2.getId());
        assertTrue(demo2.getItemQuantityMap().size() == 2);


        demo2.removeItemQuantity("item1");
        demo2.addItemQuantity("item3", Integer.valueOf(13));
        customerService.update(demo2);

        entityManager.flush();
        entityManager.clear();

        assertTrue(demo2.getItemQuantityMap().size() == 2);
    }
}
