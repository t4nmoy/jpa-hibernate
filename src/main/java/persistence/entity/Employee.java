package persistence.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private EmployeeStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedBy;

    @ManyToOne
    private Company company;

    @ManyToOne
    private Department department;

    @OneToOne
    private Profile profile;

}
