# JPA and Hibernate with Spring Boot

Reference project for demonstrating various JPA features using Hibernate as an implementation with spring boot. Basic features of JPA
include - Entity, Relationship, Various Annotations, JPQL, Entity Manager, Criteria API etc.

## List of contents

  
* [Running Tests](#running-tests)
* [Creating Abstract Base Entity](#abstract-base-entity)
* [Creating LongIdEntity Base Class](#long-id-base-entity)
* [Entity Using LongIdEntity Base Class](#entity-using-long-id-base-entity)
* [Using Lombok Annotations](#using-lombok-annotations)


## Running Tests
* ``mvn test`` run all the tests
* ``mvn -Dtest=ClassName test`` run test for a single test class
* ``mvn -Dtest=ClassName#test1 test`` run a single test from a specific class 


## Abstract base entity

```java
public abstract class IsEntity<ID> implements Serializable, HasID<ID> {

    private static final long serialVersionUID = 1L;

    IsEntity() {

    }

    protected abstract ID getId();

    protected abstract void setId(ID id);

    boolean isPersisted() {
        return getId() != null;
    }

    public void assertPersisted() {
        if (!this.isPersisted()) {
            throw new IllegalStateException("entity not persisted or loaded properly");
        }
    }

    @Override
    public ID ID() {
        return getId();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof HasID) {
            @SuppressWarnings("rawtypes")
            HasID model = (HasID) other;
            if (this.getId() != null && model.ID() != null && this.getId().equals(model.ID())) {
                return true;
            }
        }
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = 1;
        return prime * result + (getId() == null ? 0 : getId().hashCode());
    }

}

@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> extends IsEntity<ID> {
    private static final long serialVersionUID = 1L;

    @Version
    protected Long version;

    public BaseEntity() {
    }

    public BaseEntity(ID id) {
        setId(id);
    }
}
```

## Long Id Base Entity
> If any Entity needs a ```Long``` primary key it can extends ```LongIdEntity```

```java
@MappedSuperclass
public abstract class LongIdEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    public LongIdEntity() {

    }

    protected LongIdEntity(Long id) {
        super(id);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
       this.id = id;
    }
}

```

## Entity using Long id base entity

```java
@Entity
public class Contact extends LongIdEntity {

    private String address;

    @Enumerated(EnumType.STRING)
    private ContactType contactType;

    public Contact() {

    }

    private Contact(ContactType contactType, String address) {
        this.contactType = contactType;
        this.address = address;
    }

    public static Contact of(ContactType contactType, String address) {
        return new Contact(contactType, address);
    }
}
```

## Using lombok annotations

```java
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
```
Then we can create objects using the following cde

```
Employee justin = Employee.builder()
				.email("justin@wonderland.org")
				.name("justin")
				.designation(Designation.TEAM_LEAD)
				.employeeType(EmployeeType.PERMANENT)
				.company(rootCompany.get())
				.department(department.get())
				.build();

``` 

> when we need to use ```@NoArgsConstructor``` annotation with a ```@Builder``` annotation then we must put a ```@AllArgsConstructor``` annotation also, because builder pattern needs a all args constructor internally