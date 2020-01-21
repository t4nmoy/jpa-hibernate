package persistence.entity;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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

    /**
     * if any entity graph needs to contain more than one <code>EAGER</code> collections
     * then {@link LazyCollection} annotation is needed
     */
    @OneToMany(mappedBy = "department")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Employee> employees;

    public Department() {

    }

    private Department(String title, Company company) {
        this.title = title;
        this.company = company;
    }

    private Department(String title, Employee manager, Company company) {
        this(title, company);
        this.manager = manager;
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

    public static Department of(String title, Employee manager, Company company) {
        return new Department(title, manager, company);
    }

    @Override
    public String toString() {
        return "Department{" +
                "id='" + this.getId() + '\'' +
                "title='" + title + '\'' +
                '}';
    }
}
