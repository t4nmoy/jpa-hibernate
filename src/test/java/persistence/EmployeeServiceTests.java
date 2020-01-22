package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
		Employee employee = employeeService.create("john@wonderland.org", "john", Designation.BASIC_EMPLOYEE, EmployeeType.PERMANENT);

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
			Employee alice = employeeService.findMust(testData.getAlice().getId());
			Employee bob = employeeService.findMust(testData.getBob().getId());

			alice.setCompany(rootCompany);
			employeeService.update(alice);

			bob.setCompany(rootCompany);
			employeeService.update(bob);

			return rootCompany;
		});

		entityManager.flush();
		entityManager.clear();

		Company rootCompany = companyService.findMust(company.get().getId());
		assertNotNull(rootCompany);
		assertEquals(2, rootCompany.getEmployees().size());
	}

}
