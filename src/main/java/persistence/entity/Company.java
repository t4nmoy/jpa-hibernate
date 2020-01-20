package persistence.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "code" })})
public class Company extends LongIdEntity {

    private String name;

    private String code;

    private CompanyType companyType;

    @OneToMany(
            mappedBy = "company",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Employee> employees;

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
