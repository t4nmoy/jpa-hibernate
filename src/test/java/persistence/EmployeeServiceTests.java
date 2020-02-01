package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.*;
import persistence.query.CustomCriteria;
import persistence.query.QueryOperation;
import persistence.service.CompanyService;
import persistence.service.DepartmentService;
import persistence.service.EmployeeService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeServiceTests {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private TestData testData;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
    void setup() {
		testData.apply();
	}

	@Test
	@Transactional
	void curdEmployeeTest() {

		Optional<Company> rootCompany = companyService.findByCode(Company.ROOT_COMPANY);
        assertTrue(rootCompany.isPresent());

		Optional<Department> department = departmentService.findByCode("ROOT_DEPT");
		assertTrue(department.isPresent());

		Employee employee = Employee.builder()
				.email("john@wonderland.org")
				.name("john")
				.designation(Designation.BASIC_EMPLOYEE)
				.employeeType(EmployeeType.PERMANENT)
				.company(rootCompany.get())
				.department(department.get())
				.build();


		employee = employeeService.create(employee);

		assertNotNull(employee.getId());
		assertEquals("john@wonderland.org", employee.getEmail());

		EmployeeType oldType = employee.getEmployeeType();
		EmployeeType newType = EmployeeType.anyWithout(oldType);
		employee.setEmployeeType(newType);
		employeeService.update(employee);

		entityManager.flush();
		entityManager.clear();

		Optional<Employee> emp = employeeService.findOne(employee.getId());
		assertTrue(emp.isPresent());

		assertEquals(newType, employee.getEmployeeType());
	}

	@Test
	@Transactional
	void testEmployeeCompanyRelationship() {

		String demoCompanyCode = "DEMO12";

		companyService.create(demoCompanyCode, "a new demo company", CompanyType.ENGINEERING);
		entityManager.flush();
		entityManager.clear();

		Optional<Company> demoCompany = companyService.findByCode(demoCompanyCode);
		assertTrue(demoCompany.isPresent());

		demoCompany.map(company -> {
			Department demoDept1 = Department.builder()
					.code("DEPT1")
					.title("demo department 1")
					.company(company)
					.build();
			demoDept1 = departmentService.create(demoDept1);

			Department demoDept2 = Department.builder()
					.code("DEPT2")
					.title("demo dept 2")
					.company(company)
					.build();
			departmentService.create(demoDept2);

			Employee demoEmployee1 = Employee.builder()
					.email("demoEmp1@wonderland.com")
					.name("demo emp 1")
					.designation(Designation.BASIC_EMPLOYEE)
					.employeeType(EmployeeType.INTERN)
					.department(demoDept1)
					.age((short)45)
					.company(company)
					.build();
			employeeService.create(demoEmployee1);

			Employee demoEmployee2 = Employee.builder()
					.email("demoEmp2@wonderland.com")
					.name("demo emp 2")
					.designation(Designation.BASIC_EMPLOYEE)
					.employeeType(EmployeeType.PERMANENT)
					.department(demoDept1)
					.age((short)32)
					.company(company)
					.build();
			employeeService.create(demoEmployee2);

			return company;
		});

		entityManager.flush();
		entityManager.clear();

		Company rootCompany = companyService.findMust(demoCompany.get().getId());
		assertNotNull(rootCompany);
		assertEquals(2, rootCompany.getEmployees().size());


		List<CustomCriteria> criteriaList = Collections.singletonList(
				new CustomCriteria("name", "demo emp 1", QueryOperation.EQUAL)
		);
		List<Employee> employees = employeeService.findAll(criteriaList);
		assertEquals(1, employees.size());

		criteriaList = Collections.singletonList(
				new CustomCriteria("age", (short)30, QueryOperation.GREATER_THAN)
		);
		employees = employeeService.findAll(criteriaList);
		assertEquals(2, employees.size());


		criteriaList = Collections.singletonList(
				new CustomCriteria("age", (short)30, QueryOperation.LESS_THAN)
		);
		employees = employeeService.findAll(criteriaList);
		assertEquals(0, employees.size());


		criteriaList = Collections.singletonList(
				new CustomCriteria("name", "demo", QueryOperation.MATCH_END)
		);
		employees = employeeService.findAll(criteriaList);
		assertEquals(2, employees.size());
	}

	@Test
	@Transactional
	void testCriteriaApi() {
		Optional<Company> rootCompany = companyService.findByCode(Company.ROOT_COMPANY);
		assertTrue(rootCompany.isPresent());

		Optional<Department> department = departmentService.findByCode("ROOT_DEPT");
		assertTrue(department.isPresent());

		Employee jody = Employee.builder()
				.email("jody@wonderland.org")
				.name("jody")
				.designation(Designation.HR)
				.employeeType(EmployeeType.INTERN)
				.company(rootCompany.get())
				.department(department.get())
				.build();

		jody = employeeService.create(jody);
		assertNotNull(jody.getId());

		Employee colin = Employee.builder()
				.email("colin@wonderland.org")
				.name("colin")
				.designation(Designation.MANAGER)
				.employeeType(EmployeeType.PERMANENT)
				.company(rootCompany.get())
				.department(department.get())
				.build();

		colin = employeeService.create(colin);
		assertNotNull(colin.getId());

		Employee justin = Employee.builder()
				.email("justin@wonderland.org")
				.name("justin")
				.designation(Designation.TEAM_LEAD)
				.employeeType(EmployeeType.PERMANENT)
				.company(rootCompany.get())
				.department(department.get())
				.build();

		justin = employeeService.create(justin);
		assertNotNull(justin.getId());

		List<Employee> employees = employeeService.findEmployeeByEmails(Arrays.asList(jody.getEmail(), justin.getEmail()));
		assertEquals(2, employees.size());
	}

}
