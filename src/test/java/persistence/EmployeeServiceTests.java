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

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EmployeeServiceTests {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceTests.class);

	@Autowired
	private EmployeeService employeeService;

	@BeforeEach
	public void setup() {
	}

	@Test
	@Transactional
	void curdEmployee() {
		Employee employee = employeeService.save("bob@wonderland.org", "bob", Designation.BASIC_EMPLOYEE, EmployeeType.PERMANENT);
		assertNotNull(employee);
	}


	@Test
	@Transactional
	public void test2() {
	}

}
