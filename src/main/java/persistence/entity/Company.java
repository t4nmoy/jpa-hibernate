package persistence.entity;

import org.hibernate.annotations.BatchSize;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Company extends LongIdEntity {

    public static final String ROOT_COMPANY = "root";

    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    /**
     * use {@link CompanyTypeConverter} for enum type conversion
     */
    private CompanyType companyType;

    @OneToMany(
            mappedBy = "company",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @BatchSize(size = 10)
    private final List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private final List<Department> departments = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @OrderBy(value = "name desc")
    private final List<Customer> customers = new ArrayList<>();

    public Company() {

    }

    private Company(String code, String name, CompanyType companyType) {
        this.code = code;
        this.name = name;
        this.companyType = companyType;
    }

    public static Company of(String code, String name, CompanyType companyType) {
        return new Company(code, name, companyType);
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void addEmployee(Employee employee) {
        Assert.isTrue(employee.getCompany() == null || this.equals(employee.getCompany()),
                String.format("employee's company must be null or should be %s", this));

        if (!employees.contains(employee)) {
            employees.add(employee);
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id='" + this.getId() + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", companyType='" + companyType + '\'' +
                '}';
    }
}
