package persistence.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    @Enumerated(EnumType.ORDINAL)
    private EmployeeStatus status;

    @Enumerated(EnumType.STRING)
    private Designation designation;

    private EmployeeType employeeType;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private Employee(String email, String name, Designation designation, EmployeeType employeeType){
        this.email = email;
        this.name = name;
        this.designation = designation;
        this.employeeType = employeeType;
    }

    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    @ManyToOne
    private Company company;

    @ManyToOne
    private Department department;

    @OneToOne
    private Profile profile;

    public static Employee of(String email, String name, Designation designation, EmployeeType employeeType) {
        return new Employee(email, name, designation, employeeType);
    }

}
