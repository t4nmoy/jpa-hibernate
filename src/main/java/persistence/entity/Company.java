package persistence.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Company extends LongIdEntity {

    private String name;

    private String code;

    private CompanyType companyType;

    @OneToMany
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
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
