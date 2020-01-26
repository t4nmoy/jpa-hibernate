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
