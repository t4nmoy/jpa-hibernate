package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.Company;
import persistence.entity.Designation;
import persistence.entity.Employee;
import persistence.entity.EmployeeType;
import persistence.service.CompanyService;
import persistence.service.EmployeeService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeServiceTests {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceTests.class);

	@Autowired
	private EmployeeService employeeService;

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
	void curdEmployeeTest() {
		Employee employee = employeeService.save("john@wonderland.org", "john", Designation.BASIC_EMPLOYEE, EmployeeType.PERMANENT);

		assertNotNull(employee);
		assertNotNull(employee.getId());
		assertEquals("john@wonderland.org", employee.getEmail());

		EmployeeType oldType = employee.getEmployeeType();
		EmployeeType newType = EmployeeType.anyWithout(oldType);
		employee.setEmployeeType(newType);
		employeeService.update(employee);

		Optional<Employee> emp = employeeService.findOne(employee.getId());
		assertTrue(emp.isPresent());

		assertEquals(newType, employee.getEmployeeType());

	}

	@Test
	@Transactional
	public void testEmployeeCompanyRelationship() {
		Optional<Company> company = companyService.findByCode(Company.ROOT_COMPANY);
		assertTrue(company.isPresent());

		company.map(rootCompany -> {
			Employee alice = employeeService.findOne(testData.getAlice().getId()).get();
			Employee bob = employeeService.findOne(testData.getBob().getId()).get();

//			alice.setCompany(rootCompany);
//			employeeService.update(alice);
//
//			bob.setCompany(rootCompany);
//			employeeService.update(bob);

			rootCompany.getEmployees().add(alice);
			rootCompany.getEmployees().add(bob);

			companyService.update(rootCompany);

			return rootCompany;
		});

		entityManager.flush();


		logger.info("fetching root company ");

		Company rootCompany = companyService.findOne(company.get().getId()).get();
		assertNotNull(rootCompany);
		assertEquals(2, rootCompany.getEmployees().size());

	}

}
