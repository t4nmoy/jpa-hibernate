package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.*;
import persistence.service.CompanyService;
import persistence.service.CustomerService;
import persistence.utils.TenantContext;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private TestData testData;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setup(){
        testData.apply();
    }

    @Test
    @Transactional
    void curdCustomerTest() {
        Optional<Company> rootCompany = companyService.findByCode(Company.ROOT_COMPANY);
        assertTrue(rootCompany.isPresent());

        Customer demo3 = new Customer("demo3", rootCompany.get());
        demo3 = customerService.create(demo3);

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
        assertEquals(2, demo1.getPhones().size());
        assertNotNull(demo1.getCreatedDate());

        PhoneNumber num3 = new PhoneNumber("0000003", PhoneNumber.Type.OFFICE);
        demo1.addPhone(num3);
        demo1 = customerService.findMust(demo1.getId());

        customerService.update(demo1);

        entityManager.flush();
        entityManager.clear();

        assertEquals(3, demo1.getPhones().size());


        demo2.addItemQuantity("item1", 11);
        demo2.addItemQuantity("item2", 12);
        customerService.update(demo2);

        entityManager.flush();
        entityManager.clear();

        demo2 = customerService.findMust(demo2.getId());
        assertEquals(2, demo2.getItemQuantityMap().size());


        demo2.removeItemQuantity("item1");
        demo2.addItemQuantity("item3", 13);
        customerService.update(demo2);

        entityManager.flush();
        entityManager.clear();

        demo2 = customerService.findMust(demo2.getId());
        assertEquals(2, demo2.getItemQuantityMap().size());


        rootCompany = companyService.findByCode(Company.ROOT_COMPANY);
        assertTrue(rootCompany.isPresent());
        assertEquals(rootCompany.get().getCustomers().get(0), demo3);
    }


    @Test
    @Transactional
    void testDataFilterWithAop() {
        Optional<Company> rootCompany = companyService.findByCode(Company.ROOT_COMPANY);
        assertTrue(rootCompany.isPresent());

        Customer demoCustomer1 = new Customer("demo", rootCompany.get());
        demoCustomer1 = customerService.create(demoCustomer1);
        assertNotNull(demoCustomer1.getId());

        Company demoCompany = companyService.create("demo company", "root company", CompanyType.MARKETING);
        TenantContext.setTenantId(demoCompany.getId());

        Customer demoCustomer2 = new Customer("demo", demoCompany);
        demoCustomer2 = customerService.create(demoCustomer2);
        assertNotNull(demoCustomer2.getId());

        demoCustomer2.setTenantId(demoCompany.getId());
        customerService.update(demoCustomer2);

        entityManager.flush();
        entityManager.clear();

        Customer customer = customerService.findByName("demo");
        assertEquals(demoCustomer2, customer);
    }

    @Test
    @Transactional
    void testNativeQuery() {
        Optional<Company> rootCompany = companyService.findByCode(Company.ROOT_COMPANY);
        assertTrue(rootCompany.isPresent());

        Customer demoCustomer1 = new Customer("demoCustomer1", rootCompany.get());

        List<Contact> contactsForCustomer1= Arrays.asList(
                Contact.of(ContactType.HOME, "andromeda"),
                Contact.of(ContactType.HOME, "milky way"));

        demoCustomer1.setContacts(contactsForCustomer1);
        demoCustomer1 = customerService.create(demoCustomer1);
        assertNotNull(demoCustomer1.getId());

        Customer demoCustomer2 = new Customer("demoCustomer2", rootCompany.get());
        demoCustomer2 = customerService.create(demoCustomer2);
        assertNotNull(demoCustomer2.getId());

        entityManager.flush();
        entityManager.clear();

        demoCustomer1 = customerService.findMust(demoCustomer1.getId());
        demoCustomer1.getContacts().remove(0);
        customerService.update(demoCustomer1);

        entityManager.flush();
        entityManager.clear();

        customerService.delete(demoCustomer1.getId());

        entityManager.flush();
        entityManager.clear();

    }
}
