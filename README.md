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
* [Adding Custom Methods To All Repositories And Using Spring Data Specification](#adding-custom-methods-to-all-repositories-and-using-spring-data-specification)
* [Using Modifying Annotation](#using-modifying-annotation)
* [Using Criteria Api](#using-criteria-api)
* [Using BatchSize Annotation To Solve N + 1 Problem](#using-batch-size-annotation)
* [Entity Auditing Using Spring Data](#entity-auditing-using-spring-data)


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

## Adding Custom Methods To All Repositories And Using Spring Data Specification

To add custom methods to all repositories first we need to create an interface annotated with @NoRepositoryBean as follows

```java
@NoRepositoryBean
public interface ExtendedBaseRepository <T, ID extends Serializable> extends ExtendedJpaRepository<T, ID> {
    List<T> findAll(List<CustomCriteria> filters);
}
```
Then we need to implemented it as follows

```java
public class ExtendedBaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ExtendedBaseRepository<T, ID> {

    private Map<QueryOperation, CriteriaOperation<T>> operations = new ConcurrentHashMap<>();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ExtendedBaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
       super(entityInformation, entityManager);
       this.registerQueryOperations();
    }

    private void registerQueryOperations() {
        operations.put(QueryOperation.GREATER_THAN, new GreaterThanOperation<>());
        operations.put(QueryOperation.EQUAL, new EqualsOperation<>());
        operations.put(QueryOperation.LESS_THAN, new LessThanOperation<>());
        operations.put(QueryOperation.GREATER_THAN_EQUAL, new GreaterThanEqualOperation<>());
        operations.put(QueryOperation.LESS_THAN_EQUAL, new LessThanEqualOperation<>());
        operations.put(QueryOperation.NOT_EQUAL, new NotEqualOperation<>());
        operations.put(QueryOperation.MATCH, new MatchOperation<>());
        operations.put(QueryOperation.MATCH_START, new MatchStartOperation<>());
        operations.put(QueryOperation.MATCH_END, new MatchEndOperation<>());
        operations.put(QueryOperation.CONTAINS, new ContainsOperation<>());
        operations.put(QueryOperation.NOT_CONTAINS, new NotContainsOperation<>());
    }

    private Specification<T> getSpecification(List<CustomCriteria> criteriaList) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (CustomCriteria criteria : criteriaList) {
                if (operations.get(criteria.getOperation()) == null) {
                    throw new OperationNotSupportedException(
                            String.format("no handler is registered for operation : %s",
                            criteria.getOperation())
                    );
                }
                predicates.add(operations.get(criteria.getOperation()).toPredicate(root, builder, criteria));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }


    @Override
    public List<T> findAll(List<CustomCriteria> filters) {
        return this.findAll(getSpecification(filters));
    }
}
```

Now we need to enable it from the configuration class 

```java
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableJpaRepositories(repositoryBaseClass = ExtendedBaseRepositoryImpl.class)
public class JpaHibernateApplication {

}
```

For using spring data ```Specification``` we need to pass an instance of it to the spring data methods.
To generate an instance of ```Specification``` here we add some helper methods. We register all the supported filters as follows

```
 private void registerQueryOperations() {
        operations.put(QueryOperation.GREATER_THAN, new GreaterThanOperation<>());
        operations.put(QueryOperation.EQUAL, new EqualsOperation<>());
        operations.put(QueryOperation.LESS_THAN, new LessThanOperation<>());
        operations.put(QueryOperation.GREATER_THAN_EQUAL, new GreaterThanEqualOperation<>());
        operations.put(QueryOperation.LESS_THAN_EQUAL, new LessThanEqualOperation<>());
        operations.put(QueryOperation.NOT_EQUAL, new NotEqualOperation<>());
        operations.put(QueryOperation.MATCH, new MatchOperation<>());
        operations.put(QueryOperation.MATCH_START, new MatchStartOperation<>());
        operations.put(QueryOperation.MATCH_END, new MatchEndOperation<>());
        operations.put(QueryOperation.CONTAINS, new ContainsOperation<>());
        operations.put(QueryOperation.NOT_CONTAINS, new NotContainsOperation<>());
    }
 ```
 
 So, now we can implement our custom repository method as follows
 
 ```
  @Override
     public List<T> findAll(List<CustomCriteria> filters) {
         return this.findAll(getSpecification(filters));
     }
```    

## Using Modifying Annotation

```
    @Query(value = "update customer set type = ?2 where company_id = ?1", nativeQuery = true)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void changeType(Long companyId, String customerType);

    @Query(value = "update customer set number = (select next_no.no from " +
            "(select ifnull(max(number), 0) + 1 as no from customer) next_no) " +
            "where id = ?1", nativeQuery = true)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void updateNumber(Long customerId);
```

```
    @Test
    @Transactional
    void testModifyingQuery() {
        Optional<Company> rootCompany = companyService.findByCode(Company.ROOT_COMPANY);
        assertTrue(rootCompany.isPresent());

        Customer demoCustomer1 = new Customer("demo customer 1", CustomerType.LOYAL, rootCompany.get());
        customerService.create(demoCustomer1);

        entityManager.flush();
        entityManager.clear();

        demoCustomer1 = customerService.findMust(demoCustomer1.getId());
        demoCustomer1.setType(CustomerType.DISCOUNT);

        Company company = companyService.findMust(rootCompany.get().getId());
        company.setCode("1111111");

        customerService.changeType(company.getId(), CustomerType.WANDERING);

        rootCompany.get().setCode("2222222");
        companyService.update(company);
        entityManager.flush();
    }
    
```

## Using Criteria Api

```
  public List<Employee> findEmployeeByEmails(List<String> emails) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        Root<Employee> employee = query.from(Employee.class);
        Path<String> emailPath = employee.get("email");
        query.select(employee).where(builder.or(emails.stream().map(email -> builder.equal(emailPath, email)).toArray(Predicate[]::new)));
        return entityManager.createQuery(query).getResultList();
    }
```

## Using Batch Size Annotation

We cas use ```@BatchSize``` annotation to solve n + 1 query problem to optimize performance if necessary.
Generated query will use sql in operator to achieve this.

```
@OneToMany(
            mappedBy = "company",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @BatchSize(size = 10)
    private final List<Employee> employees = new ArrayList<>();
```  

## Entity Auditing Using Spring Data

Declare ```AuditableEntity``` base class

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
abstract class AuditableEntity extends LongIdEntity {

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    @LastModifiedBy
    private Long lastModifiedBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

}
```

Implement a class(```AuditorAwareImpl```) which implements ```AuditorAware``` interface. ```getCurrentAuditor```
method should return the id of current logged in user or should return something according to your application logic 

```java
@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.of(10L);
    }
}
```
Then enable auditing by putting ```@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")``` in
configuration file as follows

```java
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableJpaRepositories(repositoryBaseClass = ExtendedBaseRepositoryImpl.class)
public class JpaHibernateApplication {
}
```