package persistence.entity;

import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "code" })})
public class Company extends LongIdEntity {

    public static final String ROOT_COMPANY = "root";

    private String name;

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
    private List<Employee> employees = new ArrayList<>();

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

    public void addEmployee(Employee employee) {
        if (!employees.contains(employee)) {
            employees.add(employee);
        }
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
