package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.Designation;
import persistence.entity.Employee;
import persistence.entity.EmployeeType;
import persistence.service.EmployeeService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeServiceTests {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceTests.class);

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private AppTestData appTestData;



	@BeforeEach
	public void setup() {
		appTestData.apply();
	}

	@Test
	@Transactional
	void curdEmployee() {
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
	public void test2() {

	}

}
