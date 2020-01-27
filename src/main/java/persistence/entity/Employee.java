package persistence.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends AuditableEntity {

    @NonNull
    @NotEmpty(message = "email is required")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    private String name;

    private Short age;

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

    @NonNull
    @NotNull(message = "company must not be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    public Company company;

    @NonNull
    @NotNull(message = "department is required")
    @ManyToOne(optional = false)
    private Department department;

    @OneToOne
    private Profile profile;

    public void setCompany(Company company) {
        this.company = company;
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
