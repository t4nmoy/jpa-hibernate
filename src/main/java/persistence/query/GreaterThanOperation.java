package persistence.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@SuppressWarnings("unchecked")
public class GreaterThanOperation<T> implements CriteriaOperation <T> {
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaBuilder builder, CustomCriteria criteria) {
        return builder.greaterThan(root.get(criteria.getKey()), (Comparable) criteria.getValue());
    }
}
