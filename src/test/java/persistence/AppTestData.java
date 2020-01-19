package persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.CompanyType;
import persistence.entity.Designation;
import persistence.entity.Employee;
import persistence.entity.EmployeeType;
import persistence.service.CompanyService;
import persistence.service.EmployeeService;

import java.util.stream.IntStream;

@Service
@Transactional
public class AppTestData {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployeeService employeeService;

    public void apply() {
        createCompanies();
        createEmployees();
    }

    public void createCompanies() {

        companyService.save("root", "root company", CompanyType.PROVIDER);

        IntStream.range(0, 50)
                .forEach(value -> {
                    String code = String.valueOf(value);
                    String name = String.format("name-%s", value);
                    CompanyType companyType = CompanyType.values()[value % 3];
                    companyService.save(code, name, companyType);
                });
    }

    public void createEmployees() {
        employeeService.save("bob@wonderland.org", "bob", Designation.BASIC_EMPLOYEE, EmployeeType.PERMANENT);
        employeeService.save("alice@wonderland.org", "alice", Designation.HR_MANAGER, EmployeeType.PERMANENT);
        employeeService.save("mark@wonderland.com", "mark", Designation.TEAM_LEAD, EmployeeType.PERMANENT);
        employeeService.save("tom@gmail.org", "tom", Designation.PROJECT_MANAGER, EmployeeType.ON_PROBATION);

        IntStream.range(0, 50)
                .forEach(value -> {
                    String email = String.format("%s@gmail.org", value);
                    String name = String.valueOf(value);
                    Designation designation = Designation.values()[value % 4];
                    EmployeeType employeeType = EmployeeType.values()[value % 4];
                    employeeService.save(email, name, designation, employeeType);
                });
    }
}
