package persistence.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
public class Department extends LongIdEntity {

    private String title;

    @OneToOne
    private Employee manager;

    @OneToOne(optional = false)
    private Company company;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    public Department() {

    }

    public Department(String title, Company company) {
        this.title = title;
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public Employee getManager() {
        return manager;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }
}
