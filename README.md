# JPA and Hibernate with Spring Boot

Reference project for demonstrating various JPA features using Hibernate as an implementation with spring boot. Basic features of JPA
include - Entity, Relationship, Various Annotations, JPQL, Entity Manager, Criteria API etc.

## List of contents

  
* [Running Tests](#running-tests)
* [Creating abstract base entity](#abstract-base-entity)
* [Creating long id base entity](#long-id-base-entity)


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