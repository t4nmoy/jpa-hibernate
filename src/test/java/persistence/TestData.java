package persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.*;
import persistence.service.CompanyService;
import persistence.service.EmployeeService;

import java.util.stream.IntStream;

@Service
@Transactional
public class TestData {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployeeService employeeService;

    private Employee bob;

    private Employee alice;

    private Employee mark;

    private Employee tom;

    public void apply() {
        createCompanies();
        createEmployees();
    }

    public void createCompanies() {
        companyService.create("root", "root company", CompanyType.PROVIDER);
        IntStream.range(0, 5)
                .forEach(value -> {
                    String code = String.valueOf(value);
                    String name = String.format("name-%s", value);
                    CompanyType companyType = CompanyType.values()[value % 3];
                    companyService.create(code, name, companyType);
                });
    }

    public void createEmployees() {

//        Employee marketingManager = employeeService.create("violet@wonderland.com", "violet",
//                Designation.MARKETING_MANAGER, EmployeeType.PERMANENT);
     //   Department marketingDept = departmentService.create("MKT", "marketing dept", marketingManager, demo1);

//        this.bob = employeeService.create("bob@wonderland.org", "bob", Designation.BASIC_EMPLOYEE, EmployeeType.PERMANENT);
//        this.alice = employeeService.create("alice@wonderland.org", "alice", Designation.HR_MANAGER, EmployeeType.PERMANENT);
//        this.mark = employeeService.create("mark@wonderland.com", "mark", Designation.TEAM_LEAD, EmployeeType.PERMANENT);
//        this.tom = employeeService.create("tom@gmail.org", "tom", Designation.PROJECT_MANAGER, EmployeeType.ON_PROBATION);
//
//        IntStream.range(0, 5)
//                .forEach(value -> {
//                    String email = String.format("%s@gmail.org", value);
//                    String name = String.valueOf(value);
//                    Designation designation = Designation.values()[value % 4];
//                    EmployeeType employeeType = EmployeeType.values()[value % 4];
//                    employeeService.create(email, name, designation, employeeType);
//                });
    }

    public Employee getBob() {
        return this.bob;
    }

    public Employee getAlice() {
        return this.alice;
    }

    public Employee getMark() {
        return this.mark;
    }

    public Employee getTom() {
        return this.tom;
    }
}
