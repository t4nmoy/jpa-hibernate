# JPA and Hibernate with Spring Boot

Reference project for demonstrating various JPA features using Hibernate as an implementation with spring boot. Basic features of JPA
include - Entity, Relationship, Various Annotations, JPQL, Entity Manager, Criteria API etc.

## List of contents

  
* [Running Tests](#running-tests)
* [Creating Abstract Base Entity](#abstract-base-entity)
* [Creating LongIdEntity Base Class](#long-id-base-entity)
* [Entity Using LongIdEntity Base Class](#entity-using-long-id-base-entity)
* [Using Lombok Annotations](#using-lombok-annotations)
* [Using Custom Enum Converter](#using-custom-enum-converter)
* [Multi Tenancy Using Spring Data Filter And AOP](#multi-tenancy-using-spring-data-filter-and-aop)



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

## Using Custom Enum Converter

We can use custom enum converter for a enum type entity property. 

```java
@Converter(autoApply = true)
public class CompanyTypeConverter implements AttributeConverter<CompanyType, String> {

    @Override
    public String convertToDatabaseColumn(CompanyType companyType) {
        if (companyType == null) {
            return null;
        }
        return companyType.getCode();
    }

    @Override
    public CompanyType convertToEntityAttribute(String code) {
        if (code == null){
            return null;
        }
        return Stream.of(CompanyType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}

```

ex: ```private CompanyType companyType;```

## Multi Tenancy Using Spring Data Filter And AOP
                 
Declare an ```@Aspect``` annotated class as follows

```java
@Aspect
public class RepositoryAspect {

    private final Logger logger = LoggerFactory.getLogger(RepositoryAspect.class);

    @Autowired
    private EntityManager entityManager;

    @Pointcut("execution(public !void org.springframework.data.repository.Repository+.save*(..)) && args(tenantEntity,..)")
    public void publicSaveRepositoryMethodPointcut(TenantEntityBase tenantEntity) {
    }

    @Before(value = "publicSaveRepositoryMethodPointcut(tenantEntity)", argNames = "tenantEntity")
    public void publicSaveRepositoryMethod(TenantEntityBase tenantEntity) {
        if (TenantContext.getTenantId() != null){
            tenantEntity.setTenantId(TenantContext.getTenantId());
            logger.debug("tenant id {} inserted into entity", TenantContext.getTenantId());
        }
    }

    @Pointcut("execution(public * org.springframework.data.repository.Repository+.find*(..))")
    public void publicFindRepositoryMethodPointcut() {
    }

    @Around("publicFindRepositoryMethodPointcut()")
    public Object publicFindEntityRepositoryMethod(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Session session = entityManager.unwrap(Session.class);
            if (session != null && TenantContext.getTenantId() != null){
                Filter filter = session.enableFilter("tenantFilter");
                if (filter != null){
                    filter.setParameter("tenantId", TenantContext.getTenantId());
                }
            }
        }
        catch (Exception ex){
            throw new PersistenceException(ex.getMessage(), ex);
        }

        return pjp.proceed();
    }
}
```

Add a configuration class as follows

```java
@Configuration
public class RepositoryAspectConfiguration {

    @Bean
    public RepositoryAspect repositoryAspect() {
        return new RepositoryAspect();
    }
}
```

Declare a tenant base class

```java
@MappedSuperclass
public class TenantEntityBase extends AuditableEntity {

    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}

```

Now extends from this base class and put the following code on top of the entity class

```
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = "long"))
@Filter(name = "tenantFilter", condition = "tenant_id =:tenantId")
```

Declare an entity class as follows

```java
@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = "long"))
@Filter(name = "tenantFilter", condition = "tenant_id =:tenantId")
@Table(name = "customer")
public class Customer extends TenantEntityBase {

    @NonNull
    @NotBlank(message = "customer name is required")
    private String name;

    @Setter
    private Long number;

    @NonNull
    @Setter
    @NotNull(message = "customer type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CustomerType type;

    @NonNull
    @NotNull(message = "company is required")
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Integer> itemQuantityMap = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<PhoneNumber> phones = new HashSet<>();

    @Setter
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Contact> contacts = new ArrayList<>();

    public Map<String, Integer> getItemQuantityMap() {
        return Collections.unmodifiableMap(itemQuantityMap);
    }

    public void addItemQuantity(String itemName, Integer quantity) {
        if (this.itemQuantityMap.containsKey(itemName)) {
            return;
        }
        this.itemQuantityMap.put(itemName, quantity);
    }

    public void removeItemQuantity(String itemName) {
        this.itemQuantityMap.remove(itemName);
    }

    public Set<PhoneNumber> getPhones() {
        return Collections.unmodifiableSet(phones);
    }

    public void addPhone(PhoneNumber phone) {
        this.phones.add(phone);
    }
}
```

Then we can use the entity as follows

```
    @Test
    @Transactional
    void testDataFilterWithAop() {
        Optional<Company> rootCompany = companyService.findByCode(Company.ROOT_COMPANY);
        assertTrue(rootCompany.isPresent());

        Customer demoCustomer1 = new Customer("demo", CustomerType.DISCOUNT, rootCompany.get());
        demoCustomer1 = customerService.create(demoCustomer1);
        assertNotNull(demoCustomer1.getId());

        Company demoCompany = companyService.create("demo company", "root company", CompanyType.MARKETING);
        TenantContext.setTenantId(demoCompany.getId());

        Customer demoCustomer2 = new Customer("demo", CustomerType.LOYAL, demoCompany);
        demoCustomer2 = customerService.create(demoCustomer2);
        assertNotNull(demoCustomer2.getId());

        demoCustomer2.setTenantId(demoCompany.getId());
        customerService.update(demoCustomer2);

        entityManager.flush();
        entityManager.clear();

        Customer customer = customerService.findByName("demo");
        assertEquals(demoCustomer2, customer);
    }
```

For a web app which is protected by an authorization token we can parse the jwt token and get the tenant id from 
the token and set the tenant id using ```TenantContext.setTenantId(companyId);```



