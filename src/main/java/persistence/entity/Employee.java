package persistence.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "email" }))
public class Employee extends LongIdEntity {

    private String email;

    private String name;

    /**
     * column will be of type {@link Integer}
     *
     * 1) no way to change the order of the enum values without touching database
     * 2) new enum values can't be inserted in the middle of the enum list(it will break the meaning of enum values)
     *
     */
    @Enumerated(EnumType.ORDINAL)
    private EmployeeStatus status;

    /**
     * column will be of type {@link String}
     *
     * 1) order of the enum can be changed without touching database
     * 2) new enum values can be inserted in the middle of the enum list
     *
     */
    @Enumerated(EnumType.STRING)
    private Designation designation;

    /**
     * {@link EmployeeTypeConverter} will be used for conversion
     */
    private EmployeeType employeeType;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    private Department department;

    @OneToOne
    private Profile profile;

    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    public Employee() {

    }

    private Employee(String email, String name, Designation designation, EmployeeType employeeType){
        this.email = email;
        this.name = name;
        this.designation = designation;
        this.employeeType = employeeType;
    }

    public static Employee of(String email, String name, Designation designation, EmployeeType employeeType) {
        return new Employee(email, name, designation, employeeType);
    }

    public String getEmail() {
        return email;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + this.getId() + '\'' +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
