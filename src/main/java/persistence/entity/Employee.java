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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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

    private Employee(Builder builder) {
        this.company = builder.company;
        this.department = builder.department;
        this.email = builder.email;
        this.name = builder.name;
        this.status = builder.status;
        this.designation = builder.designation;
        this.employeeType = builder.employeeType;
        this.profile = builder.profile;
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

    public void setCompany(Company company) {
        this.company = company;
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

    public static class Builder {
        private final Company company;
        private final Department department;
        private final String email;

        private String name;
        private EmployeeStatus status;
        private Designation designation;
        private EmployeeType employeeType;

        private Profile profile;

        public Builder(Company company, Department department, String email) {
            this.company = company;
            this.department = department;
            this.email = email;
        }

        public Builder withStatus(EmployeeStatus status) {
            this.status = status;
            return this;
        }

        public Builder withDesignation(Designation designation) {
            this.designation = designation;
            return this;
        }

        public Builder withEmployeeType(EmployeeType employeeType) {
            this.employeeType = employeeType;
            return this;
        }

        public Builder withProfile(Profile profile) {
            this.profile = profile;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Employee build() {
            return new Employee(this);
        }


    }
}
