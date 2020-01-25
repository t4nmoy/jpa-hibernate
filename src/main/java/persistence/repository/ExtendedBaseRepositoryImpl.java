package persistence.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import persistence.query.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtendedBaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ExtendedBaseRepository<T, ID> {

    private Map<QueryOperation, CriteriaOperation<T>> queryOperations = new ConcurrentHashMap();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ExtendedBaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
       super(entityInformation, entityManager);
       this.registerQueryOperations();
    }

    private void registerQueryOperations() {
        queryOperations.put(QueryOperation.GREATER_THAN, new GreaterThanOperation<T>());
        queryOperations.put(QueryOperation.EQUAL, new EqualsOperation<T>());
    }

    public Specification<T> getSpecification(List<CustomCriteria> criteriaList) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (CustomCriteria criteria : criteriaList) {
                predicates.add(queryOperations.get(criteria.getOperation()).toPredicate(root, builder, criteria));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }


    @Override
    public List<T> findAll(List<CustomCriteria> filters) {
        return this.findAll(getSpecification(filters));
    }
}
