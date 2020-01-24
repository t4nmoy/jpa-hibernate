package persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.*;
import persistence.service.CompanyService;
import persistence.service.DepartmentService;
import persistence.service.EmployeeService;

import java.util.stream.IntStream;

@Service
@Transactional
public class TestData {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    private Employee bob;

    private Employee alice;

    private Employee mark;

    private Employee tom;

    public void apply() {
        createCompanies();
        createEmployees();
    }

    private void createCompanies() {
        companyService.create(Company.ROOT_COMPANY, "root company", CompanyType.MARKETING);
    }

    private void createEmployees() {
        Company rootCompany = companyService.findByCode(Company.ROOT_COMPANY).get();

        Department rootMarketingDept = Department.builder()
                .code("MKT")
                .title("marketing dept")
                .company(rootCompany)
                .build();
        rootMarketingDept = departmentService.create(rootMarketingDept);

        Department rootSalesDept = Department.builder()
                .code("SALES")
                .title("sales dept")
                .company(rootCompany)
                .build();
        departmentService.create(rootSalesDept);

        Employee violet = Employee.builder()
                .email("violet@wonderland.com")
                .name("violet")
                .designation(Designation.MANAGER)
                .employeeType(EmployeeType.PERMANENT)
                .department(rootMarketingDept)
                .company(rootCompany)
                .build();
        employeeService.create(violet);

        Employee bob = Employee.builder()
                .email("bob@wonderland.org")
                .name("bob")
                .designation(Designation.BASIC_EMPLOYEE)
                .employeeType(EmployeeType.PERMANENT)
                .department(rootMarketingDept)
                .company(rootCompany)
                .build();
        this.bob = employeeService.create(bob);


        Employee alice = Employee.builder()
                .email("alice@wonderland.org")
                .name("alice")
                .designation(Designation.BASIC_EMPLOYEE)
                .employeeType(EmployeeType.INTERN)
                .company(rootCompany)
                .department(rootSalesDept)
                .build();
        this.alice = employeeService.create(alice);



        Company tesla = companyService.create("TESLA", "tesla inc", CompanyType.ENGINEERING);

        Department teslaEngineeringDept = Department.builder()
                .code("TESLA_ENG")
                .title("tesla engineering dept")
                .company(tesla)
                .build();
        teslaEngineeringDept = departmentService.create(teslaEngineeringDept);


        Employee mark = Employee.builder()
                .email("mark@wonderland.com")
                .name("mark")
                .company(tesla)
                .department(teslaEngineeringDept)
                .designation(Designation.TEAM_LEAD)
                .employeeType(EmployeeType.PERMANENT)
                .build();
        this.mark = employeeService.create(mark);


        Department teslaQaDept = Department.builder()
                .code("TESLA_QA")
                .title("tesla QA dept")
                .company(tesla)
                .build();
        teslaQaDept = departmentService.create(teslaQaDept);

        Employee tom = Employee.builder()
                .email("tom@gmail.org")
                .name("tom")
                .designation(Designation.MANAGER)
                .employeeType(EmployeeType.ON_PROBATION)
                .department(teslaQaDept)
                .company(tesla)
                .build();
        this.tom = employeeService.create(tom);


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
