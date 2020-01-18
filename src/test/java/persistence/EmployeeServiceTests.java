package persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import persistence.entity.Designation;
import persistence.entity.Employee;
import persistence.entity.EmployeeType;
import persistence.service.EmployeeService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EmployeeServiceTests {

	@Autowired
	private EmployeeService employeeService;

	@Test
	void contextLoads() {

		Employee employee = employeeService.save("bob@wonderland.org", "bob", Designation.BASIC_EMPLOYEE, EmployeeType.PERMANENT);

		assertNotNull(employee);
	}

}
