package persistence.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Employee extends LongIdEntity {

    private String email;

    private String name;

    @Enumerated(EnumType.ORDINAL)
    private EmployeeStatus status;

    @Enumerated(EnumType.STRING)
    private Designation designation;

    private EmployeeType employeeType;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @ManyToOne
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
