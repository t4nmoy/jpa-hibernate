package persistence.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "code" })})
public class Company extends LongIdEntity {

    public static final String ROOT_COMPANY = "root";

    private String name;

    private String code;

    private CompanyType companyType;

    @OneToMany(
            mappedBy = "company",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
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
